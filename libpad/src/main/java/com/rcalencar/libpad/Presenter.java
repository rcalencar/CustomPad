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

import android.content.Intent;

class Presenter implements Contract.Presenter {
    private final Contract.View mView;
    private String mCurrentInput;

    public Presenter(Contract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void setCurrentInput(String currentInput) {
        this.mCurrentInput = currentInput;
    }

    @Override
    public String getCurrentInput() {
        return mView.getNumber();
    }

    @Override
    public void start() {
        mView.showNumber(mCurrentInput);
    }

    @Override
    public void resultOK() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(PadActivity.RESULT_DIAL, getCurrentInput());
        mView.returnOK(returnIntent);
    }
}
