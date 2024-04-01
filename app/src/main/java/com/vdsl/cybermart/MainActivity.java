package com.vdsl.cybermart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.vdsl.cybermart.Favourite.Favourite_Fragment;
import com.vdsl.cybermart.Home.View.HomeFragment;
import com.vdsl.cybermart.Message.ChatsFragment;
import com.vdsl.cybermart.Message.FragmentMessage;
import com.vdsl.cybermart.Notify.Notify_Fragment;
import com.vdsl.cybermart.Person.FragmentProfile;
import com.vdsl.cybermart.Voucher.View.VoucherActivity;
import com.vdsl.cybermart.databinding.ActivityMainBinding;

    public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    String role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onClickListenerNavBottom();
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container_main, new HomeFragment()).commit();

        SharedPreferences preferences = MainActivity.this.getSharedPreferences("Users",MODE_PRIVATE);
        role =preferences.getString("role","");
    }

    private void onClickListenerNavBottom() {
        binding.navBottom.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_bot_home) {
                General.loadFragment(getSupportFragmentManager(), new HomeFragment(), null);
            } else if (item.getItemId() == R.id.nav_bot_marker) {
                General.loadFragment(getSupportFragmentManager(), new Favourite_Fragment(), null);
            } else if (item.getItemId() == R.id.nav_bot_notify) {
                General.loadFragment(getSupportFragmentManager(), new Notify_Fragment(), null);
            } else if (item.getItemId() == R.id.nav_bot_member) {
                General.loadFragment(getSupportFragmentManager(), new FragmentProfile(), null);
            }else if(item.getItemId() == R.id.nav_bot_chat){
                Log.e("checkIf", "onCreate: " + role );
               if (role.equals("Staff") || role.equals("Admin")){
                   General.loadFragment(getSupportFragmentManager(), new FragmentMessage(), null);
               }else{
                   General.loadFragment(getSupportFragmentManager(), new FragmentMessage(), null);
               }
            }
            return true;
        });
    }

}