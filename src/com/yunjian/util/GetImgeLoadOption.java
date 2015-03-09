package com.yunjian.util;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yunjian.activity.R;

public class GetImgeLoadOption {

	public static DisplayImageOptions normalOptions;
	
	public static DisplayImageOptions getOptions() {
		if (normalOptions == null) {
			normalOptions = getBuilder().build();
		}
		return normalOptions;
	}

	public static DisplayImageOptions.Builder getBuilder() {
		return new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.default_image)
				.showImageForEmptyUri(R.drawable.default_image)
				.showImageOnFail(R.drawable.default_image).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true);
	}

}
