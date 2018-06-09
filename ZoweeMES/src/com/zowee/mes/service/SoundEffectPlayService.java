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
 * @description ��Ч���ŷ�����
 */

public class SoundEffectPlayService {
	private static SoundPool soundPool;
	private static int MAXSTREAMS;
	private static int STREAMTYPE;
	private static int STREAMQUANTITY;

	static {
		MAXSTREAMS = 10;// ������ͬʱ����10����
		STREAMTYPE = AudioManager.STREAM_MUSIC;// ���ŵ��������ָ�ʽ
		STREAMQUANTITY = 5;// ������������
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
	 *            ����������
	 * @param rightVolume
	 *            ����������
	 * @param priority
	 *            ���ȼ� ��0���
	 * @param isLoop
	 *            �Ƿ�ѭ�� 0�ǲ�ѭ����-1����Զѭ��
	 * @param rate
	 *            ���ŵ����� 0.5-2.0֮�䡣1Ϊ�����ٶ�
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
	 * @description ���ż���ͷɨ��ʱ����Ч
	 */
	public static void playLaserSoundEffect(int resId) {

		playStream(resId, 1, 1, 5, 0, 1);// ��������Ϊ
	}

	/**
	 * @description ��ʼ����Ч���� ��ֹ��һ�μ��� �޷�������Ч
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
