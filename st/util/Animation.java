package facemywrath.st.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import facemywrath.st.main.Showtime;

@SuppressWarnings("rawtypes")
public class Animation<T> {

	public TreeMap<Long, ArrayList<Frame>> frames = new TreeMap<>();
	private Showtime main;
	private Boolean looping = false;
	private Long loopDelay = 1L;
	private List<T> running = new ArrayList<>();

	public Animation(Showtime main) {
		this.main = main;
	}

	public void stop(T object)
	{
		if(running.contains(object))
			running.remove(object);
	}

	public Animation<T> addFrame(Consumer<T> frameFunction, Long delay) {
		Frame frame = new Frame<T>(this, frameFunction, delay, 1);
		if(!frames.containsKey(delay))
			frames.put(delay, new ArrayList<>());
		ArrayList<Frame> temp = frames.get(delay);
		temp.add(frame);
		frames.put(delay, temp);
		return this;
	}

	public Animation<T> addFrame(Consumer<T> frameFunction, Long delay, int repeat) {
		Frame frame = new Frame<T>(this, frameFunction, delay, repeat);
		if(!frames.containsKey(delay))
			frames.put(delay, new ArrayList<>());
		ArrayList<Frame> temp = frames.get(delay);
		temp.add(frame);
		frames.put(delay, temp);
		return this;
	}

	public void animate(T object) {
		running.add(object);
		run(object, 0, 1);
	}

	public boolean isRunning(T object)
	{
		return running.contains(object);
	}

	public Animation<T> setLooping(Boolean looping) {
		this.looping = looping;
		return this;
	}

	public Animation<T> setLooping(Boolean looping, Long loopDelay) {
		this.looping = looping;
		this.loopDelay = loopDelay;
		return this;
	}

	public Animation<T> setLoopDelay(Long loopDelay) {
		this.loopDelay = loopDelay;
		return this;
	}

	protected List<Long> keyList() {
		return frames.keySet().stream().collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private void run(T object, int i, int repeat) {
		if (i >= frames.size()) {
			if (looping && running.contains(object))
				main.getServer().getScheduler().runTaskLater(main, () -> animate(object), loopDelay);
			running.remove(object);
			return;
		}
		List<Frame> temp = frames.get(keyList().get(i));
		for(Frame frame : temp) {
			main.getServer().getScheduler().runTaskLater(main, () -> {
				if(running.contains(object))
				{
					frame.run(object);
					run(object, (repeat >= frame.getRepetitions() ? i + 1 : i),
							repeat < frame.getRepetitions() ? repeat + 1 : 1);
				}
			}, frame.getDelay());
		}
	}

	public long getNearestFrame(long tick) {
		long val = 0;
		try {
			val = frames.ceilingKey(tick);
		}catch(Exception e) {}
		return val;
	}

	public String getDuration() {
		float duration = frames.lastKey();
		int seconds = (int) (duration / 20f);
		int minutes = seconds / 60;
		DecimalFormat format = new DecimalFormat("00");
		return format.format(minutes) + ":" + format.format(seconds%60);
	}
}

class Frame<T> {
	private Consumer<T> frameFunction;
	private Long delay;
	private int repeat;

	public Frame(Animation<T> anim, Consumer<T> fun, Long del, int repeat) {
		delay = del - anim.getNearestFrame(del);
		this.frameFunction = fun;
		this.repeat = repeat;
	}

	public Long getDelay() {
		return this.delay;
	}

	public void run(T object) {
		frameFunction.accept(object);
	}

	public int getRepetitions() {
		return this.repeat;
	}
}

