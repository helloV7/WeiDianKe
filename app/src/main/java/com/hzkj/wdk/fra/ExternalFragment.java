package com.hzkj.wdk.fra;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.alipay.sdk.app.PayTask;
import com.hzkj.wdk.act.MainActivity;
import com.hzkj.wdk.model.PayResult;
import com.hzkj.wdk.utils.JiaFenConstants;
import com.hzkj.wdk.utils.SignUtils;
import com.hzkj.wdk.utils.Utils;
import com.hzkj.wdk.utils.UtilsLog;
import com.lidroid.xutils.BitmapUtils;
import com.hzkj.wdk.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * 支付宝
 * @author howie
 *
 */
public class ExternalFragment extends BaseFragment implements OnClickListener {

	// 商户PID
	public static  String PARTNER ;
	// 商户收款账号
	//public static final String SELLER = "1013630675@qq.com";
	public static  String SELLER ;
	// 商户私钥，pkcs8格式
	//public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMZwvvINm0lKrUvRWFIhjtOO0DRLqShR41i7VrmCyewtwpmsknIaQnfIzkcXMH52MO/EYEzSLYqL9SZZkw85/XL5JVNKTqDMvq+gjq/RYInrTNvBTBI/y1U3cP9BwuxksxBvS9bVipkEiyHo1l5xrD0+Bh6vjAzZnLtFT1mBsN5tAgMBAAECgYEAlx7De2h7SqxxHt0Vaq6dq2UGD91SvB7oiOlaGTqN9au/l/JBMF3pInmtBZyWVKhDikS9paMmBW2iSwtKH/1kkfOoKjG/vD3HZtLZKkc24Ry99YmrmdNLp8gr1LOeXp1jAlwaYmCLm9WHTrfb6N3WxJARrkg1BUr9U7hEAzGRZUECQQDrU6gBDx9hRhWWtmWjfwUaOuBrcpwTkPu+yIvP0ksVXnTwni2vglgiezE6tZZMLbNcQftnKRJPX2JXV4Kn7n09AkEA19+KIWtMw7kgK1bpcXGYVKMn6pLmYfSLQe4Tw+lYYyrlnkgREGrTuud5O4Uk0HeYt356yjAjps09+2x6XaBY8QJADbo4j69BJlx/P+Zt8Wakdo4+ryXlYuLPPKbYOQxMYve6nJqtkZJp78D98y3KkbEMyHH052Sm88hdpTiun3szBQJBAITS4ibV4pG64nRIMbK/dj79dJ38bQxwbw7N8hNlDCFYT5goN3emg0hJDGr27UPnkmu0wtCM9iK3vh9pC5F4C8ECQE7CRhVGaQgIQRWOuxK4cRpHKpQz3Qc2ETQyFENfSfs3YQuM1V9UxCs7d5XtRd8ahnAM550c4YQec9/Xg/2yisA=";
	public static  String RSA_PRIVATE;
	//公钥：MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDGcL7yDZtJSq1L0VhSIY7TjtA0S6koUeNYu1a5gsnsLcKZrJJyGkJ3yM5HFzB+djDvxGBM0i2Ki/UmWZMPOf1y+SVTSk6gzL6voI6v0WCJ60zbwUwSP8tVN3D/QcLsZLMQb0vW1YqZBIsh6NZecaw9PgYer4wM2Zy7RU9ZgbDebQIDAQAB
	private static final int SDK_PAY_FLAG = 1;

	private static final int SDK_CHECK_FLAG = 2;
	private MainActivity mActivity;
	private Button pay;
	private String paySn="",name="",price="";
	private ImageView iv_logo,back;
	private BitmapUtils bitmapUtil;
	//private GoodsDetailModel prodectModel;
	private TextView product_subject,product_price;
	private int goodsNum=1;
	private boolean luckNotify=false;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				System.out.println("111");
				PayResult payResult = new PayResult((String) msg.obj);
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();
				UtilsLog.d("====","result==="+resultInfo);
				String resultStatus = payResult.getResultStatus();
				UtilsLog.d("====","result1==="+resultStatus);
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Utils.toastShow(mActivity, "支付成功");
					Intent mIntent = new Intent(JiaFenConstants.GET_DATA);
					mActivity.sendBroadcast(mIntent);
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Utils.toastShow(mActivity, "支付结果确认中");
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Utils.toastShow(mActivity, "支付失败");
					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Utils.toastShow(mActivity, "检查结果为：" + msg.obj);
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity= (MainActivity) getActivity();
		bitmapUtil=new BitmapUtils(mActivity);
		Bundle b = getArguments();
		//prodectModel=(GoodsDetailModel)b.getSerializable("model");
		name=b.getString("name");
		price=b.getString("price");
		paySn=b.getString("paySn");
	}

	public void setData(String sname,String sprice,String spaysn,MainActivity act,boolean luck){
		name=sname;
		price=sprice;
		paySn=spaysn;
		mActivity=act;
		bitmapUtil=new BitmapUtils(mActivity);
		luckNotify=luck;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.pay_external, null);
		mActivity.FrameLayoutVisible(true);
		initView(v);
		return v;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v==pay){
			pay();
		}else if(v==back){
			mActivity.backFragment();
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Intent mIntent = new Intent(JiaFenConstants.GET_DATA);
		mActivity.sendBroadcast(mIntent);
	}

	private void initView(View v){
		pay=(Button)v.findViewById(R.id.pay);
		pay.setOnClickListener(this);
		iv_logo=(ImageView)v.findViewById(R.id.iv_logo);
		back=(ImageView)v.findViewById(R.id.back);
		back.setOnClickListener(this);
		//bitmapUtil.display(iv_logo, ""+prodectModel.getUgoods_pic());
		product_price=(TextView)v.findViewById(R.id.product_price);
		product_subject=(TextView)v.findViewById(R.id.product_subject);
		product_price.setText(""+price);
		product_subject.setText(""+name+(goodsNum>1?"等"+goodsNum+"件商品":""));
	}
	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay() {
		if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)|| TextUtils.isEmpty(SELLER)) {
			new AlertDialog.Builder(mActivity)
					.setTitle("警告")
					.setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									mActivity.backFragment();
									//finish();
								}
							}).show();
			return;
		}
		// 订单
		String orderInfo = getOrderInfo("(微点客)"+name,
				"(微点客)"+name, ""+price);

		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		if (TextUtils.isEmpty(sign)){
			Utils.toastShow(mActivity,"签名有误");
			return;
		}
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"+ getSignType();
		Log.d("====", "payinfo===" + payInfo);
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(mActivity);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check() {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(mActivity);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};
		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();
	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(mActivity);
		String version = payTask.getVersion();
		Utils.toastShow(mActivity, version);
		//Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public String getOrderInfo(String subject, String body, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + paySn + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";
		if(luckNotify) {
			// 服务器异步通知页面路径
//			orderInfo += "&notify_url=" + "\"" + "http://zfjiafen.heizitech.com/index.php?r=api/common/luckynotify"
			orderInfo += "&notify_url=" + "\"" + SERVER_URL+"?r=api/common/luckynotify"
					+ "\"";
		}else{
			orderInfo += "&notify_url=" + "\"" + SERVER_URL+"?r=api/common/newnotify"
					+ "\"";
		}

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}
	

}
