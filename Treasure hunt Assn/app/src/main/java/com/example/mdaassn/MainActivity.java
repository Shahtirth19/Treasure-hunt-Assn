package com.example.mdaassn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    MyViewPagerAdapter myViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser()== null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(MainActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if(!uid.equals("s6WsqI1777M96zRXzs8DF68pfzE3")){
                startActivity(new Intent(MainActivity.this, UserActivity.class));
                finish();
            }else{
                tabLayout = findViewById(R.id.tab_layout);
                viewPager2 =findViewById(R.id.view_pager);
                myViewPagerAdapter = new MyViewPagerAdapter(this);
                viewPager2.setAdapter(myViewPagerAdapter);

                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager2.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

                viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        tabLayout.getTabAt(position).select();
                    }
                });
            }
        }
    }
}