package com.android.tvvideo.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class ImageLoad {

	/*final static int IC_STUB= R.drawable.bg;
	final static int IC_EMPTY=R.drawable.bg;
	final static int IC_ERROR=R.drawable.bg;*/

	static DisplayImageOptions options;

	static Context con;

	public static void init(Context context){

		con=context;

		/*    File cacheDir = StorageUtils.getCacheDirectory(context);  
	    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)  
	            .memoryCacheExtraOptions(480, 800) // default = device screen dimensions  
	            .diskCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)  
	            .taskExecutor(...)  
	            .taskExecutorForCachedImages(...)  
	            .threadPoolSize(3) // default  
	            .threadPriority(Thread.NORM_PRIORITY - 1) // default  
	            .tasksProcessingOrder(QueueProcessingType.FIFO) // default  
	            .denyCacheImageMultipleSizesInMemory()  
	            .memoryCache(new LruMemoryCache(2 * 1024 * 1024))  
	            .memoryCacheSize(2 * 1024 * 1024)  
	            .memoryCacheSizePercentage(13) // default  
	            .diskCache(new UnlimitedDiscCache(cacheDir)) // default  
	            .diskCacheSize(50 * 1024 * 1024)  
	            .diskCacheFileCount(100)  
	            .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default  
	            .imageDownloader(new BaseImageDownloader(context)) // default  
	            .imageDecoder(new BaseImageDecoder()) // default  
	            .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default  
	            .writeDebugLogs()  
	            .build();  */

		//创建默认的ImageLoader配置参数  
		ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(context);

		//Initialize ImageLoader with configuration.  
		ImageLoader.getInstance().init(configuration);

		/*		//显示图片的配置  
		options = new DisplayImageOptions.Builder()  
		.showImageOnLoading(IC_STUB) // resource or drawable  
		.showImageForEmptyUri(IC_EMPTY) // resource or drawable  
		.showImageOnFail(IC_ERROR) // resource or drawable  
		.resetViewBeforeLoading(false)  // default  
		.delayBeforeLoading(1000)  
		.cacheInMemory(false) // default  
		.cacheOnDisk(false) // default  
		       .preProcessor(...)  
        .postProcessor(...)  
        .extraForDownloader(...)  
		.considerExifParams(false) // default  
		.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default  
		.bitmapConfig(Bitmap.Config.ARGB_8888) // default  
		     .decodingOptions(...)  
		.displayer(new SimpleBitmapDisplayer()) // default  
		.handler(new Handler()) // default  
		.build();  */

		//显示图片的配置    
		options = new DisplayImageOptions.Builder()
	/*	.showImageOnLoading(IC_STUB)
		.showImageOnFail(IC_ERROR) */
		.cacheInMemory(true)    
		.cacheOnDisk(true)    
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build(); 

	}

	//加载imageloader默认配置
	public static void loadDefultImage(String imageUrl, ImageView imageAware){

		imageUrl=SystemUtil.getServerUrl(con)+"/app/img/"+imageUrl;

		ImageLoader.getInstance().displayImage(imageUrl, imageAware, options);
	}

	//加载imageloader默认配置
	public static void loadImage(String imageUrl, ImageView imageAware){

		ImageLoader.getInstance().displayImage(imageUrl, imageAware, options);

	}

	//设置列表滚动不闪烁
	public static void setListScroll(ListView list, boolean pauseOnScroll, boolean pauseOnFling){
		list.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll, pauseOnFling));
	}

	//设置gridview滚动不闪烁
	public static void setGridScroll(GridView grid, boolean pauseOnScroll, boolean pauseOnFling){
		grid.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll, pauseOnFling));
	}

}
