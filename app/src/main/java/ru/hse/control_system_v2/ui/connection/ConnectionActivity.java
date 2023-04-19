package ru.hse.control_system_v2.ui.connection;

import static ru.hse.control_system_v2.AppConstants.APP_LOG_TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.CollapsingToolbarLayoutKt;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.databinding.ActivityConnectionBinding;

public class ConnectionActivity extends AppCompatActivity {

    private NavHostFragment navHostFragment;
    private NavController navController;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ru.hse.control_system_v2.databinding.ActivityConnectionBinding binding = ActivityConnectionBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    void setUpNavigation() {
        //TODO("Not yet implemented")
//        navHostFragment = (NavHostFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.nav_host_fragment);
//        if (navHostFragment != null) {
//            NavigationUI.setupWithNavController(main_bottom_menu,
//                    (navHostFragment).getNavController());
//        }
//
//        navController = Objects.requireNonNull(navHostFragment).getNavController();
//        currentVisibleFragment = navController.getCurrentDestination();
//        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
//            @Override
//            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
//                Log.e(APP_LOG_TAG, "onDestinationChanged: " + destination.getLabel());
//                //отслеживания фпагмента на главном экране
//                currentVisibleFragment = destination;
//
//            }
//        });
//
//        main_bottom_menu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.mainMenuFragment:
//                        navController.navigate(R.id.mainMenuFragment);
//                        return true;
//                    case R.id.settingsFragment:
//                        navController.navigate(R.id.settingsFragment);
//                        return true;
//                }
//                return false;
//            }
//        });
    }

//    public final NavDestination getCurrentVisibleFragment() {
//        return currentVisibleFragment;
//    }

}
