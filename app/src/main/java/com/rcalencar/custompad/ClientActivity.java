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

package com.rcalencar.custompad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rcalencar.libpad.PadActivity;

public class ClientActivity extends AppCompatActivity {
    private TextView resultText;
    private TextView inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });
        inputText = (TextView) findViewById(R.id.editText_input);
        resultText = (TextView) findViewById(R.id.editText_result);
    }


    private void open() {
        Intent i = new Intent(this, PadActivity.class);
        i.putExtra(PadActivity.INPUT_DIAL, inputText.getText().toString());
        startActivityForResult(i, PadActivity.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PadActivity.REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra(PadActivity.RESULT_DIAL);
                resultText.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "canceled", Toast.LENGTH_LONG).show();
            }
        }
    }
}
