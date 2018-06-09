package com.zowee.mes.service;

import com.zowee.mes.R;
import com.zowee.mes.app.MyApplication;
import com.zowee.mes.service.BackgroundService.Task;
import com.zowee.mes.service.BackgroundService.TaskOperator;
import com.zowee.mes.service.BackgroundService.TaskType;

import android.media.AudioManager;
import android.media.SoundPool;

/**
 * @author Administrator
 * @description 音效播放服务类
 */

public class SoundEffectPlayService {
	private static SoundPool soundPool;
	private static int MAXSTREAMS;
	private static int STREAMTYPE;
	private static int STREAMQUANTITY;

	static {
		MAXSTREAMS = 10;// 最大可以同时播放10个流
		STREAMTYPE = AudioManager.STREAM_MUSIC;// 播放的流是音乐格式
		STREAMQUANTITY = 5;// 播放流的质量
		soundPool = new SoundPool(MAXSTREAMS, STREAMTYPE, STREAMQUANTITY);
	}

	public static class SoundPoolResource {
		public static int OK_MUSIC = soundPool.load(MyApplication.getApp(),
				R.raw.ok, 1);
		public static int ERROR_MUSIC = soundPool.load(MyApplication.getApp(),
				R.raw.error, 1);
		public static int PASS_MUSIC = soundPool.load(MyApplication.getApp(),
				R.raw.ok, 1);
		public static int TJOK_MUSIC = soundPool.load(MyApplication.getApp(),
				R.raw.tjok, 1);
		public static int TJFAIL_MUSIC = soundPool.load(MyApplication.getApp(),
				R.raw.tjfail, 1);
		public static int ALARM_MUSIC = soundPool.load(MyApplication.getApp(),
				R.raw.alarm, 1);

	}

	/**
	 * @param resId
	 * @param leftVolume
	 *            左音道音量
	 * @param rightVolume
	 *            右音道音量
	 * @param priority
	 *            优先级 ，0最低
	 * @param isLoop
	 *            是否循环 0是不循环，-1是永远循环
	 * @param rate
	 *            播放的速率 0.5-2.0之间。1为正常速度
	 * @description
	 */
	public static void playStream(int resId, float leftVolume,
			float rightVolume, int priority, int isLoop, int rate) {
		if (null == soundPool)
			soundPool = new SoundPool(MAXSTREAMS, STREAMTYPE, STREAMQUANTITY);

		soundPool.play(resId, leftVolume, rightVolume, priority, isLoop, rate);
	}

	/**
	 * @param resId
	 * @description 播放激光头扫描时的音效
	 */
	public static void playLaserSoundEffect(int resId) {

		playStream(resId, 1, 1, 5, 0, 1);// 声音设置为
	}

	/**
	 * @description 初始化音效播放 防止第一次加载 无法播放音效
	 */
	public static void initSoundEffect(int resId) {
		Task initSoundEffect = new Task(null, TaskType.INIT_SOUNDEFFECT, resId);
		TaskOperator taskOperator = new TaskOperator() {
			@Override
			public Task doTask(Task task) {
				int resId = (Integer) task.getTaskData();
				playStream(resId, 0, 0, 1, 0, 2);
				task.setHandle(false);

				return task;
			}
		};

		initSoundEffect.setTaskOperator(taskOperator);
		BackgroundService.addTask(initSoundEffect);

	}

}
