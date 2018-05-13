package vn.letmewash.android.adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import vn.letmewash.android.LoginFragment;
import vn.letmewash.android.R;
import vn.letmewash.android.RegisterFragment;

/**
 * Created by camel on 11/29/17.
 */

public class LoginFragmentPagerAdapter extends FragmentPagerAdapter {

    Context mContext;

    public LoginFragmentPagerAdapter(Context mContext, FragmentManager fm) {
        super(fm);
        this.mContext = mContext;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LoginFragment();
            case 1:
                return new RegisterFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.action_login);
            case 1:
                return mContext.getString(R.string.action_register);
            default:
                return null;
        }
    }
}
