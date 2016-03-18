package com.watabou.noosa;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.nyrds.android.google.util.IabHelper;
import com.nyrds.android.google.util.IabResult;
import com.nyrds.android.google.util.Inventory;
import com.nyrds.android.google.util.Purchase;
import com.nyrds.pixeldungeon.ml.EventCollector;
import com.nyrds.pixeldungeon.ml.R;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.Preferences;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class GameWithGoogleIap extends Game {

	protected static final String SKU_LEVEL_1 = "supporter_level_1";
	protected static final String SKU_LEVEL_2 = "supporter_level_2";
	protected static final String SKU_LEVEL_3 = "supporter_level_3";

	IabHelper mHelper = null;
	Inventory mInventory = null;

	private volatile boolean m_iapReady = false;

	static InterstitialAd mSaveAndLoadAd;
	static InterstitialAd mEasyModeSmallScreenAd;

	public boolean iapReady() {
		return m_iapReady;
	}

	static final int RC_REQUEST = (int) (Math.random() * 0xffff);

	public GameWithGoogleIap(Class<? extends Scene> c) {
		super(c);
	}

	private static boolean isSmallScreen() {
		return (width() < 400 || height() < 400);
	}

	private static boolean needDisplaySmallScreenEasyModeIs() {
		return difficulty == 0 && isSmallScreen() && PixelDungeon.donated() == 0;
	}

	private static boolean googleAnalyticsUsable() {
		if(Preferences.INSTANCE.getInt(Preferences.KEY_COLLECT_STATS,0) > 0) {
			return android.os.Build.VERSION.SDK_INT >= 9;
		}
		return false;
	}

	private static boolean googleAdsUsable() {
		return android.os.Build.VERSION.SDK_INT >= 9 && !android.os.Build.BRAND.contains("chromium");
	}

	@Override
	public void displayEasyModeBanner() {
		if (googleAdsUsable() && isConnectedToInternet()) {
			if (isSmallScreen()) {
				initEasyModeIntersitial();
			} else {
				instance().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (instance().layout.getChildCount() == 1) {
							AdView adView = new AdView(instance());
							adView.setAdSize(AdSize.SMART_BANNER);
							adView.setAdUnitId(getVar(R.string.easyModeAdUnitId));
							adView.setBackgroundColor(Color.TRANSPARENT);
							AdRequest adRequest = new AdRequest.Builder().addTestDevice(getVar(R.string.testDevice))
									.build();
							instance().layout.addView(adView, 0);
							adView.loadAd(adRequest);
							needSceneRestart = true;
						}
					}
				});
			}
		}
	}

	@Override
	public  void removeEasyModeBanner() {
		if (googleAdsUsable()) {
			instance().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (instance().layout.getChildCount() == 2) {
						instance().layout.removeViewAt(0);
						needSceneRestart = true;
					}
				}

			});
		}
	}

	private static Map<InterstitialAd, Boolean> mAdLoadInProgress = new HashMap<>();

	private static void requestNewInterstitial(final InterstitialAd isAd) {

		Boolean loadAlreadyInProgress = mAdLoadInProgress.get(isAd);

		if (loadAlreadyInProgress != null && loadAlreadyInProgress) {
			return;
		}

		AdRequest adRequest = new AdRequest.Builder().addTestDevice(getVar(R.string.testDevice)).build();

		isAd.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				super.onAdClosed();
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				super.onAdFailedToLoad(errorCode);
				mAdLoadInProgress.put(isAd, false);
			}

			@Override
			public void onAdLoaded() {
				super.onAdLoaded();
				mAdLoadInProgress.put(isAd, false);
			}

			@Override
			public void onAdOpened() {
				super.onAdOpened();
			}

			@Override
			public void onAdLeftApplication() {
				super.onAdLeftApplication();
			}
		});

		mAdLoadInProgress.put(isAd, true);
		isAd.loadAd(adRequest);

	}

	private static void displayIsAd(final InterstitialPoint work, final InterstitialAd isAd) {
		instance().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (isAd == null) {
					work.returnToWork(false);
					return;
				}

				if (!isAd.isLoaded()) {
					work.returnToWork(false);
					return;
				}

				isAd.setAdListener(new AdListener() {
					@Override
					public void onAdClosed() {
						requestNewInterstitial(isAd);
						work.returnToWork(true);
					}
				});
				isAd.show();
			}
		});
	}

	public static void displaySaveAndLoadAd(final InterstitialPoint work) {
		displayIsAd(work, mSaveAndLoadAd);
	}

	public static void displayEasyModeSmallScreenAd(final InterstitialPoint work) {
		if(needDisplaySmallScreenEasyModeIs()) {
			displayIsAd(work, mEasyModeSmallScreenAd);
		} else {
			work.returnToWork(true);
		}
	}

	private static void initEasyModeIntersitial() {
		if (googleAdsUsable() && isConnectedToInternet()) {
			{
				instance().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (mEasyModeSmallScreenAd == null) {
							mEasyModeSmallScreenAd = new InterstitialAd(instance());
							mEasyModeSmallScreenAd.setAdUnitId(getVar(R.string.easyModeSmallScreenAdUnitId));
							requestNewInterstitial(mEasyModeSmallScreenAd);
						}
					}
				});
			}
		}
	}

	public void initSaveAndLoadIntersitial() {
		if (googleAdsUsable() && isConnectedToInternet()) {
			{
				instance().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (mSaveAndLoadAd == null) {
							mSaveAndLoadAd = new InterstitialAd(instance());
							mSaveAndLoadAd.setAdUnitId(getVar(R.string.saveLoadAdUnitId));
							requestNewInterstitial(mSaveAndLoadAd);
						}
					}
				});
			}
		}
	}

	public void initIapPhase2() {

		if (mHelper != null) {
			return;
		}

		if (googleAdsUsable()) {
			int st = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
			if (st != ConnectionResult.SUCCESS) {
				return;

			}
		}

		String base64EncodedPublicKey = getVar(R.string.iapKey);

		// Create the helper, passing it our context and the public key to
		// verify signatures with
		Log.d("GAME", "Creating IAB helper.");
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		// enable debug logging (for a production application, you should set
		// this to false).
		mHelper.enableDebugLogging(false);

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		Log.d("GAME", "Starting setup.");

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				Log.d("GAME", "Setup finished.");

				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					complain("Problem setting up in-app billing: " + result);
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;

				ArrayList<String> skuList = new ArrayList<>();

				skuList.add(SKU_LEVEL_1);
				skuList.add(SKU_LEVEL_2);
				skuList.add(SKU_LEVEL_3);

				mHelper.queryInventoryAsync(true, skuList, mGotInventoryListener);
			}
		});
	}

	private static boolean isConnectedToInternet() {
		InetAddress ipAddr;
		try {
			ipAddr = InetAddress.getByName("google.com");
		} catch (UnknownHostException e) {
			return false;
		}

		return !ipAddr.toString().equals("");
	}

	@Override
	public void initEventCollector() {
		if(googleAnalyticsUsable()) {
			EventCollector.init(this);
		} else {
			EventCollector.disable();
		}
	}

	public void initIap() {
		initEventCollector();

		new Thread() {
			@Override
			public void run() {
				if (isConnectedToInternet()) {
					initIapPhase2();
				}
			}
		}.start();
	}

	private void checkPurchases() {
		setDonationLevel(0);

		Purchase check = mInventory.getPurchase(SKU_LEVEL_1);
		if (check != null && verifyDeveloperPayload(check)) {
			setDonationLevel(1);
		}

		check = mInventory.getPurchase(SKU_LEVEL_2);
		if (check != null && verifyDeveloperPayload(check)) {
			setDonationLevel(2);
		}

		check = mInventory.getPurchase(SKU_LEVEL_3);
		if (check != null && verifyDeveloperPayload(check)) {
			setDonationLevel(3);
		}
	}

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		@Override
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			//Log.d("GAME", "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null)
				return;

			// Is it a failure?
			if (result.isFailure()) {
				complain("Failed to query inventory: " + result);
				return;
			}

			mInventory = inventory;
			checkPurchases();
			m_iapReady = true;
		}
	};

	public String getPriceString(int level) {
		if (mInventory == null) {
			return null;
		}

		switch (level) {
		case 1:
			return mInventory.getSkuDetails(SKU_LEVEL_1).getPrice();
		case 2:
			return mInventory.getSkuDetails(SKU_LEVEL_2).getPrice();
		case 3:
			return mInventory.getSkuDetails(SKU_LEVEL_3).getPrice();
		}
		return null;
	}

	private void doPurchase(String sku) {
		if (!m_iapReady) {
			alert("Sorry, we not ready yet");
			EventCollector.logEvent("fail","purchase not ready");
			return;
		}

		String payload = "";

		m_iapReady = false;
		mHelper.launchPurchaseFlow(this, sku, RC_REQUEST, mPurchaseFinishedListener, payload);
	}

	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();

		return true;
	}

	public abstract void setDonationLevel(int level);

	public static GameWithGoogleIap instance() {
		return (GameWithGoogleIap) Game.instance();
	}

	public static void donate(int level) {
		switch (level) {
		case 1:
			instance().doPurchase(SKU_LEVEL_1);
			break;
		case 2:
			instance().doPurchase(SKU_LEVEL_2);
			break;
		case 3:
			instance().doPurchase(SKU_LEVEL_3);
			break;
		}
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			//Log.d("GAME", "Purchase finished: " + result + ", purchase: " + purchase);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (result.isFailure()) {
				complain("Error purchasing: " + result);
				m_iapReady = true;
				return;
			}

			if (!verifyDeveloperPayload(purchase)) {
				complain("Error purchasing. Authenticity verification failed.");
				return;
			}

			//Log.d("GAME", "Purchase successful!");

			if (purchase.getSku().equals(SKU_LEVEL_1)) {
				setDonationLevel(1);
			}

			if (purchase.getSku().equals(SKU_LEVEL_2)) {
				setDonationLevel(2);
			}

			if (purchase.getSku().equals(SKU_LEVEL_3)) {
				setDonationLevel(3);
			}

			m_iapReady = true;
		}
	};

	void complain(String message) {
		EventCollector.logEvent("iap error",message);
		Log.e("GAME", "**** IAP Error: " + message);
	}

	void alert(final String message) {
		instance().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// AlertDialog.Builder bld = new
				// AlertDialog.Builder(instance());
				// bld.setMessage(message);
				// bld.setNeutralButton("OK", null);
				Log.d("GAME", "Showing alert dialog: " + message);
				// bld.create().show();
			}
		});
	}

}
