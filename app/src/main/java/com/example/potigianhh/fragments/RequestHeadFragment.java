package com.example.potigianhh.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.potigianhh.R;
import com.example.potigianhh.fragments.adapters.RequestHeadersAdapter;
import com.example.potigianhh.fragments.decorators.RequestMarginDecorator;
import com.example.potigianhh.model.RequestHeader;
import com.example.potigianhh.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class RequestHeadFragment extends BaseFragment {
    private AlertDialog dataInputDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_base, container, false);
    }

    @Override
    public void onDestroyView() {
        final RecyclerView recyclerView = getView().findViewById(R.id.requesthead_list);

        if (recyclerView.getAdapter() != null) {
            // We cleanup the activity reference
            ((RequestHeadersAdapter) recyclerView.getAdapter()).clearMainActivityReference();
        }

        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView userText = view.findViewById(R.id.requesthead_user_text);

        final Button assignCigaretteRequestButton = view.findViewById(R.id.requesthead_button_cigarettes_equests);
        final Button assignAnyRequestButton = view.findViewById(R.id.requesthead_button_others_requests);

        final RecyclerView recyclerView = view.findViewById(R.id.requesthead_list);

        getMainActivity().remove(Constants.CURRENT_REQUEST_KEY);

        String urlInit = Constants.REQUESTS_HEADERS_ASSIGNED_SIMPLE
                .replace("{preparerId}", Integer.toString(getPreparer().getId()));
        doListRequest(Request.Method.GET, urlInit, RequestHeader.class, null,
                RequestHeadFragment.this::onInitialDataReceived, null);

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.addItemDecoration(new RequestMarginDecorator( 5));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        assignCigaretteRequestButton.setOnClickListener(v -> displayPreparingAlert(true));

        assignAnyRequestButton.setOnClickListener(v -> displayPreparingAlert(false));

        verifyRecyclerViewState();

        userText.setText("Trabajando como " + getPreparer().getName());
    }

    public void clearAssignedRequests() {
        String url = Constants.REQUESTS_HEADERS_ASSIGNED_CLEAR
                .replace("{preparerId}", Integer.toString(getPreparer().getId()));
        doRequest(Request.Method.POST, url, Boolean.class, null,
                RequestHeadFragment.this::onClearedRequests, null);
    }

    private void displayPreparingAlert(boolean cigarettesOnly) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.preparing_view, null);
        EditText deliveryText = dialogView.findViewById(R.id.preparing_delivery_text);
        EditText suffixText = dialogView.findViewById(R.id.preparing_suffix_text);

        builder.setView(dialogView)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    int suffix = 0, delivery = 0;
                    try { suffix = Integer.parseInt(suffixText.getText().toString()); } catch (Exception e) {}
                    try { delivery = Integer.parseInt(deliveryText.getText().toString()); } catch (Exception e) {}
                    if (suffix == 0 && delivery == 0) {
                        displayErrorDialog("Datos inválidos", "Debes ingresar número de sufijo o reparto");
                        dataInputDialog = null;
                        return;
                    }
                    showAlertDialog(cigarettesOnly, suffix, delivery);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {});
        dataInputDialog = builder.show();
    }

    private AlertDialog showAlertDialog(boolean cigarettesOnly, int suffix, int delivery) {
        String message = "¿Desea tomar pedidos varios?";
        String onlyCigarettesStr = String.valueOf(cigarettesOnly);

        if (cigarettesOnly) {
            message = "¿Desea tomar pedidos de cigarrillos?";
        }

        return new AlertDialog.Builder(this.getContext())
                .setIcon(R.drawable.ic_warn_icon)
                .setTitle("Atención")
                .setMessage(message)
                .setPositiveButton("Si", (dialog, which) -> {
                    String url = Constants.REQUESTS_HEADERS_ASSIGNED_URL
                            .replace("{preparerId}", Integer.toString(getPreparer().getId()))
                            .replace("{onlyCigarettes}", onlyCigarettesStr)
                            .replace("{suffixNumber}", Integer.toString(suffix))
                            .replace("{deliveryNumber}", Integer.toString(delivery));
                    doListRequest(Request.Method.POST, url, RequestHeader.class, null,
                            RequestHeadFragment.this::onDataReceived, null);
                    dataInputDialog = null;
                })
                .setNegativeButton("No", (dialog, which) -> {})
                .show();
    }

    private void onClearedRequests(boolean response) {
        if (response) {
            this.displayToast("Se limpiaron los pedidos asignados.");
            final RecyclerView recyclerView = getView().findViewById(R.id.requesthead_list);
            recyclerView.setAdapter(new RequestHeadersAdapter(getMainActivity(), new ArrayList<>()));

            verifyRecyclerViewState();
        }
    }

    private void onInitialDataReceived(List<RequestHeader> requestHeaders) {
        if (requestHeaders.size() > 0) {
            final RecyclerView recyclerView = getView().findViewById(R.id.requesthead_list);
            recyclerView.setAdapter(new RequestHeadersAdapter(getMainActivity(), requestHeaders));

            verifyRecyclerViewState();
        }
    }

    private void onDataReceived(List<RequestHeader> requestHeaders) {
        if (requestHeaders.size() == 0) {
            new Handler(Looper.getMainLooper()).post(() -> {
                displayErrorDialog("Error en solicitud", "No se encontraron pedidos disponibles");
            });
            return;
        }

        final RecyclerView recyclerView = getView().findViewById(R.id.requesthead_list);
        recyclerView.setAdapter(new RequestHeadersAdapter(getMainActivity(), requestHeaders));

        verifyRecyclerViewState();
    }

    private void verifyRecyclerViewState() {
        final TextView emptyView = getView().findViewById(R.id.requesthead_empty_list);
        final RecyclerView recyclerView = getView().findViewById(R.id.requesthead_list);

        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBarcode(String content) {
        if (dataInputDialog != null) {
            EditText editText = dataInputDialog.findViewById(R.id.preparing_suffix_text);
            getMainActivity().runOnUiThread(() -> {
                editText.setText(content.trim());
                dataInputDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                getMainActivity().playSound(R.raw.ok);
            });
        }
    }
}
