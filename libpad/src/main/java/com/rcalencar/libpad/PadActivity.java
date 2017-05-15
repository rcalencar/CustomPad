/*
 * Copyright (C) 2017 Rodrigo Costa de Alencar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rcalencar.libpad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class PadActivity extends AppCompatActivity implements PadActivityFragment.OnFragmentInteractionListener {
    public static final int REQUEST_CODE = 124;
    public static final String RESULT_DIAL = "result";
    public static final String INPUT_DIAL = "input";

    private final String CURRENT_INPUT = "CURRENT_INPUT";
    private Contract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dial);

        PadActivityFragment fragment =
                (PadActivityFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (fragment == null) {
            fragment = PadActivityFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.contentFrame, fragment);
            transaction.commit();
        }

        mPresenter = new Presenter(fragment);

        if (savedInstanceState != null) {
            String currentInput = (String) savedInstanceState.getSerializable(CURRENT_INPUT);
            mPresenter.setCurrentInput(currentInput);
        } else {
            mPresenter.setCurrentInput(getIntent().getStringExtra(INPUT_DIAL));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_INPUT, mPresenter.getCurrentInput());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResultOk(Intent intent) {
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
