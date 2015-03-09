package com.yunjian.image;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class ImageLoader {

	private static ImageLoader instance;

	private ExecutorService executorService; // �̳߳�
	private ImageMemoryCache memoryCache; // �ڴ滺��
	private ImageFileCache fileCache; // �ļ�����
	private Map<String, ImageView> taskMap; // �������
	private boolean allowLoad = true; // �Ƿ��������ͼƬ

	private ImageLoader(Context context) {
		// ��ȡ��ǰϵͳ��CPU��Ŀ
		int cpuNums = Runtime.getRuntime().availableProcessors();
		// ����ϵͳ��Դ��������̳߳ش�С
		this.executorService = Executors.newFixedThreadPool(cpuNums + 1);
		this.memoryCache = new ImageMemoryCache(context);
		this.fileCache = new ImageFileCache();
		this.taskMap = new HashMap<String, ImageView>();
	}

	/**
	 * ʹ�õ�������֤����Ӧ����ֻ��һ���̳߳غ�һ���ڴ滺����ļ�����
	 */
	public static ImageLoader getInstance(Context context) {
		if (instance == null)
			instance = new ImageLoader(context);
		return instance;
	}

	/**
	 * �ָ�Ϊ��ʼ�ɼ���ͼƬ��״̬
	 */
	public void restore() {
		this.allowLoad = true;
	}

	/**
	 * ��סʱ���������ͼƬ
	 */
	public void lock() {
		this.allowLoad = false;
	}

	/**
	 * ����ʱ����ͼƬ
	 */
	public void unlock() {
		this.allowLoad = true;
		doTask();
	}

	/**
	 * �������
	 */
	public void addTask(String url, ImageView img) {
		// �ȴ��ڴ滺���л�ȡ��ȡ��ֱ�Ӽ���
		Bitmap bitmap = memoryCache.getBitmapFromCache(url);
		if (bitmap != null) {
			img.setImageBitmap(bitmap);
		} else {
			synchronized (taskMap) {
				/**
				 * ��ΪListView��GridView��ԭ�����������Ƴ���Ļ��itemȥ�����������ʾ��item,
				 * �����img��item������ݣ����������taskMap�����ʼ���ǵ�ǰ��Ļ�ڵ�����ImageView��
				 */
				img.setTag(url);
				taskMap.put(Integer.toString(img.hashCode()), img);
			}
			if (allowLoad) {
				doTask();
			}
		}
	}

	/**
	 * ���ش�������е�����ͼƬ
	 */
	private void doTask() {
		synchronized (taskMap) {
			Collection<ImageView> con = taskMap.values();
			for (ImageView i : con) {
				if (i != null) {
					if (i.getTag() != null) {
						loadImage((String) i.getTag(), i);
					}
				}
			}
			taskMap.clear();
		}
	}

	private void loadImage(String url, ImageView img) {
		this.executorService.submit(new TaskWithResult(
				new TaskHandler(url, img), url));
	}

	/*** ���һ��ͼƬ,�������ط���ȡ,�������ڴ滺��,Ȼ�����ļ�����,���������ȡ ***/
	private Bitmap getBitmap(String url) {
		// ���ڴ滺���л�ȡͼƬ
		Bitmap result = memoryCache.getBitmapFromCache(url);
		if (result == null) {
			// �ļ������л�ȡ
			result = fileCache.getImage(url);
			if (result == null) {
				// �������ȡ
				result = ImageGetFormHttp.downloadBitmap(url);
				if (result != null) {
					fileCache.saveBitmap(result, url);
					memoryCache.addBitmapToCache(url, result);
				}
			} else {
				// ��ӵ��ڴ滺��
				memoryCache.addBitmapToCache(url, result);
			}
		}
		return result;
	}

	public void externGetBitmap(String url, ImageView imageView) {
		loadImage(url, imageView);
	}

	/*** ���߳����� ***/
	private class TaskWithResult implements Callable<String> {
		private String url;
		private Handler handler;

		public TaskWithResult(Handler handler, String url) {
			this.url = url;
			this.handler = handler;
		}

		@Override
		public String call() throws Exception {
			Message msg = new Message();
			msg.obj = getBitmap(url);
			if (msg.obj != null) {
				handler.sendMessage(msg);
			}
			return url;
		}
	}

	/*** �����Ϣ ***/
	private class TaskHandler extends Handler {
		String url;
		ImageView img;

		public TaskHandler(String url, ImageView img) {
			this.url = url;
			this.img = img;
		}

		@Override
		public void handleMessage(Message msg) {
			/*** �鿴ImageView��Ҫ��ʾ��ͼƬ�Ƿ񱻸ı� ***/
			if (img.getTag().equals(url)) {
				if (msg.obj != null) {
					Bitmap bitmap = (Bitmap) msg.obj;
					img.setImageBitmap(bitmap);
				}
			}
		}
	}

}