package hdvideoplayerallformat.gallery.videoplayer.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Fragment> fragmentList = new ArrayList<>();

    public ScreenSlidePagerAdapter(ArrayList<Fragment> fragmentList, @NonNull FragmentManager fm) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (position >= 0 && position < fragmentList.size())
            return fragmentList.get(position);
        return fragmentList.get(0);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
