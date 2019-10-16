package com.example.potigianhh;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.potigianhh.exceptions.FragmentInitializationException;
import com.example.potigianhh.exceptions.FragmentNotFoundException;
import com.example.potigianhh.fragments.BaseFragment;
import com.example.potigianhh.fragments.LoginFragment;
import com.example.potigianhh.fragments.RequestHeadFragment;
import com.example.potigianhh.utils.BarcodeUtils;
import com.example.potigianhh.utils.Constants;
import com.example.potigianhh.utils.StoreService;
import com.google.common.reflect.TypeToken;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;

import java.lang.reflect.Type;
import java.util.Optional;

public class MainActivity extends AppCompatActivity implements BarcodeReader.BarcodeListener {
    private BaseFragment currentFragment;
    private AidcManager manager;
    private BarcodeReader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constants.setApiUrl(getApiUrl());

        StoreService.getInstance().cleanOldContent(this);

        // Then we simply proceed with adding other previously opened fragments (if apply)
        String lastFragmentName = get(Constants.FRAGMENT_KEY, String.class, new TypeToken<String>(){}.getType());

        BaseFragment loginFragment = new LoginFragment();
        addFragment(loginFragment);

        if (lastFragmentName != null && !loginFragment.getFragmentName().equals(lastFragmentName)) {
            replaceFragment(lastFragmentName);
        }

        BarcodeUtils.initialize(this);
    }

    @Override
    protected void onStart() {
        BarcodeUtils.claim(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        BarcodeUtils.release(this);
        super.onStop();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (getCurrentFragment().getFragmentName().equals("LoginFragment")) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_error_icon)
                    .setTitle("Menú de usuario")
                    .setMessage("Esta opción está deshabilitada en el login")
                    .setPositiveButton("Aceptar", (dialog, which) -> {})
                    .show();
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_logout:
                findFragmentByClass(LoginFragment.class).logout();
            break;
            case R.id.menu_api:
                displayApiAlert();
            break;
            case R.id.menu_printer:
                displayPrinterAlert();
            break;
            case R.id.menu_clear_requests:
                displayClearRequestsAlert();
            break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    public <T extends BaseFragment> T findFragmentByClass(Class<T> fragmentClass) {
        Optional<T> fragment = getSupportFragmentManager().getFragments()
                .stream()
                .filter(fragmentClass::isInstance)
                .map(fragmentClass::cast)
                .findFirst();

        if (!fragment.isPresent()) {
            throw new FragmentNotFoundException(fragmentClass);
        }
        else {
            return fragment.get();
        }
    }

    public <T extends BaseFragment> void replaceFragment(Class<T> fragmentClass) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            BaseFragment newFragment;

            if (currentFragment instanceof LoginFragment) {
                fragmentTransaction.hide(currentFragment);
            } else {
                fragmentTransaction.remove(currentFragment);
            }

            if (fragmentClass == LoginFragment.class) {
                newFragment = findFragmentByClass(LoginFragment.class);
                fragmentTransaction.show(newFragment);
            } else {
                newFragment = fragmentClass.newInstance();
                fragmentTransaction.add(R.id.fragment_container, newFragment, fragmentClass.getSimpleName());
            }

            currentFragment = newFragment;
            fragmentTransaction.commit();
            set(Constants.FRAGMENT_KEY, newFragment.getFragmentName());
        } catch (IllegalAccessException | InstantiationException ex) {
            throw new FragmentInitializationException(fragmentClass.getSimpleName());
        }
    }

    public void replaceFragment(String fragmentName) {
        replaceFragment(castFragmentNameToClass(fragmentName));
    }

    public void addFragment(BaseFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragment_container, fragment, fragment.getClass().getSimpleName());

        fragmentTransaction.commit();

        currentFragment = fragment;
        String lastFragment = get(Constants.FRAGMENT_KEY, String.class, new TypeToken<String>(){}.getType());
        if (lastFragment == null) {
            set(Constants.FRAGMENT_KEY, fragment.getFragmentName());
        }
    }

    @Override
    public void onBarcodeEvent(BarcodeReadEvent barcodeReadEvent) {
        if (currentFragment != null) {
            currentFragment.onBarcode(barcodeReadEvent.getBarcodeData());
        }
    }

    @Override
    public void onFailureEvent(BarcodeFailureEvent barcodeFailureEvent) {}

    public <T> T get(String key, Class<T> clazz, Type clazzType) {
        return StoreService.getInstance().get(key, clazz, clazzType, this.getApplicationContext());
    }

    public <T> void set(String key, T object) {
        StoreService.getInstance().set(key, object, this.getApplicationContext());
    }

    public void remove(String key) {
        StoreService.getInstance().remove(key, this.getApplicationContext());
    }

    public AidcManager getManager() {
        return manager;
    }

    public void setManager(AidcManager manager) {
        this.manager = manager;
    }

    public BaseFragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(BaseFragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    public BarcodeReader getReader() {
        return reader;
    }

    public void setReader(BarcodeReader reader) {
        this.reader = reader;
    }

    public String getApiUrl() {
        String url = get(Constants.API_KEY, String.class, new TypeToken<String>(){}.getType());
        if (url == null) {
            return Constants.DEFAULT_BASE;
        }
        return url;
    }

    public void setApiUrl(String newValue) {
        set(Constants.API_KEY, newValue);
        Constants.setApiUrl(newValue);
    }

    public String getPrinter() {
        String printer = get(Constants.PRINTER_KEY, String.class, new TypeToken<String>(){}.getType());
        if (printer == null)
            return "0";
        return printer;
    }

    public void setPrinter(String printer) {
        set(Constants.PRINTER_KEY, printer);
    }

    private void displayPrinterAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.printer_view, null);
        TextView currentPrinterView = dialogView.findViewById(R.id.printer_pre);
        EditText newPrinterView = dialogView.findViewById(R.id.printer_post);
        String printer = getPrinter();

        builder.setView(dialogView)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    setPrinter(newPrinterView.getText().toString());
                    Toast.makeText(this, "Se ha cambiado la impresora", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {});
        currentPrinterView.setText(printer);
        newPrinterView.setText(printer);
        builder.show();
    }

    private void displayApiAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.api_view, null);
        TextView urlView = dialogView.findViewById(R.id.api_pre_url);
        EditText newView = dialogView.findViewById(R.id.api_post_url);
        String url = getApiUrl();


        builder.setView(dialogView)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    setApiUrl(newView.getText().toString());
                    Toast.makeText(this, "Se ha cambiado la API", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {});
        newView.setText(url);
        urlView.setText(url);
        builder.show();
    }

    private void displayClearRequestsAlert() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_warn_icon)
                .setTitle("Aviso de pedidos")
                .setMessage("¿Deseas limpiar los pedidos asignados?")
                .setPositiveButton("Si", (dialog, which) -> clearAssignedRequests())
                .setNegativeButton("No", (dialog, which) -> {})
                .show();
    }

    private void clearAssignedRequests() {
        if (!getCurrentFragment().getFragmentName().equals("RequestHeadFragment")) {
            replaceFragment(RequestHeadFragment.class);
        }
        (new Handler()).postDelayed(this::doClearRequests, 1000);
    }

    private void doClearRequests() {
        ((RequestHeadFragment) currentFragment).clearAssignedRequests();
    }

    private Class<? extends BaseFragment> castFragmentNameToClass(String fragmentName) {
        String pkg = "com.example.potigianhh.fragments.";
        try {
            return Class.forName(pkg + fragmentName)
                    .asSubclass(BaseFragment.class);
        } catch (ClassNotFoundException e) {
            throw new FragmentInitializationException(fragmentName);
        }
    }
}
