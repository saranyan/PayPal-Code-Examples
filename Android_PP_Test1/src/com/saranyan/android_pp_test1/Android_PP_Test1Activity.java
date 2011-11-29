package com.saranyan.android_pp_test1;

import java.math.BigDecimal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalPayment;

 

public class Android_PP_Test1Activity extends Activity implements OnClickListener,
		OnItemSelectedListener, OnMultiChoiceClickListener, OnDismissListener {
	
	
	private CheckoutButton launchPayPalButton;
	final static public int PAYPAL_BUTTON_ID = 10001;
	private static final int REQUEST_PAYPAL_CHECKOUT = 2;
	private ProgressDialog _progressDialog;
	private boolean _paypalLibraryInit = false;
	private boolean _progressDialogRunning = false;
	private ImageView imgView;
	private Integer[] mImg = {
		R.drawable.picasso1,R.drawable.picasso2,R.drawable.picasso3	
	};
	private Integer[] mImg_cost = {
			1200,799,1450
	};
	
	private boolean[] mPurchase = {
			false,false,false
	};
	private String[] captions = {
			"Boy with a Pipe, Rose Period, $1200.00","The blue wonder, Rose Period, $799.00", "The shocking truth, Rose Period, $1450.00"
	};
	
	private int _value = 100; //some default value that is not index
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //imgView = (ImageView)findViewById(R.id.ImageView01);
        Gallery gallery = (Gallery) findViewById(R.id.gallery);
        gallery.setAdapter(new ImageAdapter(this));
        
        imgView = (ImageView)findViewById(R.id.ImageView01);
        imgView.setImageResource(mImg[0]);
        
        
        gallery.setOnItemClickListener(new OnItemClickListener() {
        	
            public void onItemClick(AdapterView parent, View v, int position, long id) {
            	 imgView.setImageResource(mImg[position]);
            	 
                if (_paypalLibraryInit) {
                	_value = position;
        			showPayPalButton();
        			Toast.makeText(Android_PP_Test1Activity.this, captions[position], Toast.LENGTH_SHORT).show();
        			
        			} 
            	else {
            		// Display a progress dialog to the user and start checking for when
        			// the initialization is completed
            		
            		
            		Thread initThread = new Thread(initLibraryRunnable);
            		initThread.start();
            		
            		_progressDialog = new ProgressDialog(Android_PP_Test1Activity.this);
        			_progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        			_progressDialog.setMessage("Loading PayPal Payment Library");
        			_progressDialog.setCancelable(false);
        			_progressDialog.show();
        			_progressDialogRunning = true;
        			Thread newThread = new Thread(checkforPayPalInitRunnable);
        			
        			newThread.start();
        			
        			_value = position;
        			Toast.makeText(Android_PP_Test1Activity.this, "Picasso Paintings", Toast.LENGTH_SHORT).show();
                	
            	}
            	
            }
        });
    }
    
   
    
    public void showPayPalButton() {
    	
    	removePayPalButton();
		// Back in the UI thread -- show the "Pay with PayPal" button
		// Generate the PayPal Checkout button and save it for later use
		PayPal pp = PayPal.getInstance();
		//get the checkoutbutton
    	launchPayPalButton = pp.getCheckoutButton(Android_PP_Test1Activity.this, PayPal.BUTTON_194x37, CheckoutButton.TEXT_PAY);
    	//add it to layout
    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	//position this at the bottom
    	params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM); 
    	//some margins for visual goodness
    	params.bottomMargin = 5;
    	params.leftMargin= 50;
    	
    	launchPayPalButton.setLayoutParams(params); 
   		launchPayPalButton.setId(PAYPAL_BUTTON_ID);
    	launchPayPalButton.setOnClickListener(Android_PP_Test1Activity.this);
    	
    	((RelativeLayout)findViewById(R.id.RelativeLayout01)).addView(launchPayPalButton);
    }
    
    
    /* this method removes the PayPal button from the view
	 */
	private void removePayPalButton() {
		// Avoid an exception for setting a parent more than once
		if (launchPayPalButton != null) {
			((RelativeLayout) findViewById(R.id.RelativeLayout01))
					.removeView(launchPayPalButton);
		}
	}
    
    
    /* method to handle PayPal checkout button onClick event
	 * - this must be called from the onClick() method implemented by the application
	 */
	public void PayPalButtonClick(View v) {
		PayPalPayment newPayment = new PayPalPayment(); 
		newPayment.setSubtotal(new BigDecimal(mImg_cost[_value])); 
		newPayment.setCurrencyType("USD"); 
		newPayment.setRecipient("svigra_1322573821_biz@gmail.com"); 
		newPayment.setMerchantName("Picasso and PayPal");					
					
		Intent checkoutIntent = PayPal.getInstance().checkout(newPayment, this /*, new ResultDelegate()*/);
			    // Use the android's startActivityForResult() and pass in our
			    // Intent.
			    // This will start the library.
		if (mPurchase[_value]) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Message");
			alertDialog.setMessage("You have already purchased this item!");
			alertDialog.show();
		}
		else {
			this.startActivityForResult(checkoutIntent, REQUEST_PAYPAL_CHECKOUT);
		}
			
		
		//Intent paypalIntent = PayPal.getInstance().checkout(newPayment, this); 
		//this.startActivityForResult(paypalIntent, 1);

	}
	
	
    //Gallery code
	//Display the Picasso paintings as a gallery
    public class ImageAdapter extends BaseAdapter {
        int mGalleryItemBackground;
        private Context mContext;

        private Integer[] mImageIds = {
                R.drawable.picasso1,
                R.drawable.picasso2,
                R.drawable.picasso3       
        };

        public ImageAdapter(Context c) {
            mContext = c;
            TypedArray attr = mContext.obtainStyledAttributes(R.styleable.Android_PP_Test1);
            mGalleryItemBackground = attr.getResourceId(
                    R.styleable.Android_PP_Test1_android_galleryItemBackground, 0);
            attr.recycle();
        }

        public int getCount() {
            return mImageIds.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(mContext);

            imageView.setImageResource(mImageIds[position]);
            imageView.setLayoutParams(new Gallery.LayoutParams(150, 100));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundResource(mGalleryItemBackground);

            return imageView;
        }
    }
    
    /** init method **/
    public void initLibrary() {
    	PayPal pp = PayPal.getInstance();
    	if (pp == null) {
    		// This is the main initialization call that takes in your Context,
    		// the Application ID, and the server you would like to connect to.
			pp = PayPal.initWithAppID(this, "APP-80W284485P519543T",
					PayPal.ENV_SANDBOX);

			// -- These are required settings.
			pp.setLanguage("en_US"); // Sets the language for the library.
			// --

			// -- These are a few of the optional settings.
			// Sets the fees payer. If there are fees for the transaction, this
			// person will pay for them. Possible values are FEEPAYER_SENDER,
			// FEEPAYER_PRIMARYRECEIVER, FEEPAYER_EACHRECEIVER, and
			// FEEPAYER_SECONDARYONLY.
			pp.setFeesPayer(PayPal.FEEPAYER_EACHRECEIVER);
			// Set to true if the transaction will require shipping.
			pp.setShippingEnabled(true);
			// Dynamic Amount Calculation allows you to set tax and shipping
			// amounts based on the user's shipping address. Shipping must be
			// enabled for Dynamic Amount Calculation. This also requires you to
			// create a class that implements PaymentAdjuster and Serializable.
			pp.setDynamicAmountCalculationEnabled(false);
			// --
			_paypalLibraryInit = true;
			
			//if dialog is running, close it
			if (_progressDialog.isShowing()) {
        		_progressDialog.dismiss();
        		_progressDialogRunning = false;
        	}
    	}
    }
    
 // PayPal Activity Results. This handles all the responses from the PayPal
 	// Payments Library
 	@Override
 	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
 		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
 		if (requestCode == REQUEST_PAYPAL_CHECKOUT) {
 			mPurchase[_value] = true;
 			
 			alertDialog.setTitle("Success");
			alertDialog.setMessage("This item will be shipped to you in one week!");
			alertDialog.show();
			
 		} else {
 			super.onActivityResult(requestCode, resultCode, intent);
 		}

 	}
 	
 	/* This method handles the PayPal Activity Results. This handles all the responses from the PayPal
	 * Payments Library.
	 *  This method must be called from the application's onActivityResult() handler
	 */
	public void PayPalActivityResult(int requestCode, int resultCode, Intent intent) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Test...");
		alertDialog.setMessage("This is your ID " + resultCode);
		alertDialog.show();

	}
    
    /**********************************
	 * PayPal library related methods
	 **********************************/

	// This lets us show the PayPal Button after the library has been
	// initialized
	final Runnable showPayPalButtonRunnable = new Runnable() {
		public void run() {
			showPayPalButton();
		}
	};

	// This lets us run a loop to check the status of the PayPal Library init
	final Runnable checkforPayPalInitRunnable = new Runnable() {
		public void run() {
			checkForPayPalLibraryInit();
		}
	};
	
	//This lets us run the initLibrary function
	final Runnable initLibraryRunnable = new Runnable() {
		public void run() {
			initLibrary();
		}
	};

	// This method is called if the Review page is being loaded but the PayPal
	// Library is not
	// initialized yet.
	private void checkForPayPalLibraryInit() {
		// Loop as long as the library is not initialized
		while (_paypalLibraryInit == false) {
			try {
				// wait 1/2 a second then check again
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// Show an error to the user
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Error initializing PayPal Library")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// Could do anything here to handle the
										// error
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
		// If we got here, it means the library is initialized.
		// So, add the "Pay with PayPal" button to the screen
		runOnUiThread(showPayPalButtonRunnable);
	}


	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		// TODO Auto-generated method stub
		
	}


	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
	}


	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		if (v == (CheckoutButton) findViewById(PAYPAL_BUTTON_ID)) {
			PayPalButtonClick(v);
		}
		
		
	}
	
}