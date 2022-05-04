package com.hws.hwsappandroid.ui.me;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hws.hwsappandroid.MyOrderActivity;
import com.hws.hwsappandroid.R;
import com.hws.hwsappandroid.SearchActivity;
import com.hws.hwsappandroid.databinding.FragmentMeBinding;

public class MeFragment extends Fragment {

    private FragmentMeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MeViewModel meViewModel = new ViewModelProvider(this).get(MeViewModel.class);

        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // buttons for my orders
        binding.buttonCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), MyOrderActivity.class);
                startActivity(i);
            }
        });
        binding.buttonWaitPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), MyOrderActivity.class);
                i.putExtra("tab", 1);
                startActivity(i);
            }
        });
        binding.buttonWaitSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), MyOrderActivity.class);
                i.putExtra("tab", 2);
                startActivity(i);
            }
        });
        binding.buttonWaitGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), MyOrderActivity.class);
                i.putExtra("tab", 3);
                startActivity(i);
            }
        });
        binding.buttonCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), MyOrderActivity.class);
                i.putExtra("tab", 4);
                startActivity(i);
            }
        });

        // buttons for my service
        binding.buttonFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonAfterSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonAddressManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonEnterpriseBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonCreditCardManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
        binding.buttonCustomerService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.not_implemented, Toast.LENGTH_SHORT).show();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}