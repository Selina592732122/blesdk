package com.shenghao.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.shenghao.R;


public class ComingSoonActivity extends BaseActivity {
    public static final String BUNDLE_TITLE = "title_name";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coming_soon);
        initViews();
    }

    @Override
    protected void initViews() {
        super.initViews();
        TextView titleNameTv = findViewById(R.id.titleNameTv);
        titleNameTv.setText(getIntent().getStringExtra(BUNDLE_TITLE));
    }

}
