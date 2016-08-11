package com.qian.pos;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qian.pos.util.BitmapUtil;
import com.qian.pos.util.FileUtils;
import com.qian.pos.util.PictureUtil;
import com.qian.pos.util.UploadUtil;
import com.qian.pos.util.UploadUtil.OnUploadProcessListener;
import com.qian.servletasynchttp.R;

public class ImageUploadActivity extends Activity// implements OnUploadProcessListener
{
	private static final String TAG = "uploadImage";
	protected static final int TO_UPLOAD_FILE = 1;  
	protected static final int UPLOAD_FILE_DONE = 2;  
	public static final int TO_SELECT_PHOTO = 3;
	private static final int UPLOAD_INIT_PROCESS = 4;
	private static final int UPLOAD_IN_PROCESS = 5;
	private static String requestURL = "http://114.55.72.18/UnionPay/UploadAction";
	private Button uploadButton;
	//private ProgressBar progressBar;
	
	private String picPath = null;
	private ProgressDialog progressDialog;
	
	
	private GridView list_gv;
	private MyAdapter adapter;
	
	private HashMap<Integer,Bitmap> imageMap = new HashMap<Integer, Bitmap>();
	private HashMap<Integer,String> filePathMap = new HashMap<Integer, String>();
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TO_UPLOAD_FILE:
				toUploadFile();
				break;
			case UPLOAD_INIT_PROCESS:
				//progressBar.setMax(msg.arg1);
				break;
			case UPLOAD_IN_PROCESS:
				//progressBar.setProgress(msg.arg1);
				break;
			case UPLOAD_FILE_DONE:
				String result = "响应码："+msg.arg1+"\n响应信息："+msg.obj+"\n耗时："+UploadUtil.getRequestTime()+"秒";
				
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
    }
	private void initView() {
        uploadButton = (Button) this.findViewById(R.id.uploadImage);
        uploadButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(picPath!=null)
				{
					handler.sendEmptyMessage(TO_UPLOAD_FILE);
				}else{
					Toast.makeText(ImageUploadActivity.this, "上传的文件路径出错", Toast.LENGTH_LONG).show();
				}
				
			}
		});
        progressDialog = new ProgressDialog(this);
        list_gv = (GridView) findViewById(R.id.gv_image);
        adapter = new MyAdapter();
        list_gv.setAdapter(adapter);
        list_gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				switch (position) {
				case 0:
					intent = new Intent(ImageUploadActivity.this,SelectPicActivity.class);
					startActivityForResult(intent, 0);
					break;
				case 1:
					intent = new Intent(ImageUploadActivity.this,SelectPicActivity.class);
					startActivityForResult(intent, 1);
					break;
				case 2:
					intent = new Intent(ImageUploadActivity.this,SelectPicActivity.class);
					startActivityForResult(intent, 2);
					break;
				case 3:
					intent = new Intent(ImageUploadActivity.this,SelectPicActivity.class);
					startActivityForResult(intent, 3);
					break;
				case 4:
					intent = new Intent(ImageUploadActivity.this,SelectPicActivity.class);
					startActivityForResult(intent, 4);
					break;
				}
				
			}
        	
		});
	}
	
	
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(ImageUploadActivity.this, R.layout.item_grid, null);
			ImageView image = (ImageView) view.findViewById(R.id.item_grida_image);
			
			Iterator<Integer> iterator = imageMap.keySet().iterator();
			while(iterator.hasNext()) {
				Integer next = iterator.next();
				if(next.intValue() == position) {
					image.setImageBitmap(imageMap.get(next));
				}
			}
			TextView textView = (TextView) view.findViewById(R.id.tv_explain);
			switch (position) {
			case 0:
				textView.setText("身份证正面");
				break;
			case 1:
				textView.setText("身份证反面");
				break;
			case 2:
				textView.setText("营业执照");
				break;
			case 3:
				textView.setText("商铺门口照片");
				break;
			case 4:
				textView.setText("商铺内部照片");
				break;
			default:
				break;
			}
			
			
			return view;
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==Activity.RESULT_OK) {
			picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
			Log.i(TAG, "最终选择的图片="+picPath);
		//	Toast.makeText(getApplicationContext(), "最终选择的图片="+picPath, 0).show();
			Bitmap bm = BitmapFactory.decodeFile(picPath);
			//Bitmap tempBitmap = BitmapUtil.createImageThumbnail(picPath,128);//压缩图片
			//Bitmap saveBitmap = BitmapUtil.createImageThumbnail(picPath,2048);
			Bitmap tempBitmap = PictureUtil.getSmallBitmap(picPath, 128, 128);//压缩图片
			Bitmap saveBitmap = PictureUtil.getSmallBitmap(picPath,1280,720);//上传服务器的bitmap 手机横着拍照
			String path = Environment.getExternalStorageDirectory()+ "/pos/"+requestCode+".JPEG";
			FileUtils.saveBitmap(saveBitmap, requestCode+"");
			filePathMap.put(requestCode, path);
			imageMap.put(requestCode, tempBitmap);
			//Toast.makeText(ImageUploadActivity.this, "第"+requestCode+"张图片", 0).show();
		//	System.out.println("imageMap"+imageMap.size());
		//	System.out.println("filePathMap"+filePathMap.size());
			adapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void toUploadFile()
	{
		progressDialog.setMessage("正在上传文件...");
		progressDialog.show();
		final String fileKey = "upload";
		final UploadUtil uploadUtil = UploadUtil.getInstance();;
		uploadUtil.setOnUploadProcessListener(new OnUploadProcessListener() {
			
			@Override
			public void onUploadProcess(int uploadSize) {
				Message msg = Message.obtain();
				msg.what = UPLOAD_IN_PROCESS;
				msg.arg1 = uploadSize; 
				handler.sendMessage(msg);
			}
			
			@Override
			public void onUploadDone(int responseCode, String message) {

				progressDialog.dismiss();
				Message msg = Message.obtain();
				msg.what = UPLOAD_FILE_DONE;
				msg.arg1 = responseCode;
				msg.obj = message;
				handler.sendMessage(msg);
			}
			
			@Override
			public void initUpload(int fileSize) {
				Message msg = Message.obtain();
				msg.what = UPLOAD_INIT_PROCESS;
				msg.arg1 = fileSize;
				handler.sendMessage(msg);
			}
		});  //设置监听器监听上传状态
		
		final Map<String, String> params = new HashMap<String, String>();
		params.put("dpnumber", "13800001111");
		
		System.out.println(filePathMap.size());
		
	
		new Thread(new Runnable() {
			 int i = 0;
			@Override
			public void run() {
			
					final boolean uploadFile = uploadUtil.uploadFile(filePathMap,fileKey,requestURL,params);
			
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							if(uploadFile) {
								Toast.makeText(ImageUploadActivity.this, "上传成功", 0).show();
								progressDialog.dismiss();
							} else {
								Toast.makeText(ImageUploadActivity.this, "上传失败", 0).show();
								progressDialog.dismiss();
							}
							
						}
					});
					
				}	
				
			
		}).start();
			
				
			
			
		}
		
	}
	

