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
import android.graphics.Point;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CustomPadInstrumentedTest {
    private static final String BASIC_SAMPLE_PACKAGE = "com.rcalencar.libpad.test";

    @Rule
    public ActivityTestRule<PadActivity> mActivityRule = new ActivityTestRule<>(PadActivity.class);
    private UiDevice mDevice;

    @Test
    public void enterDigits() throws Exception {
        // Click on the edit task button
        onView(withId(R.id.digit0)).perform(click());
        onView(withId(R.id.digit1)).perform(click());
        onView(withId(R.id.digit2)).perform(click());
        onView(withId(R.id.digit3)).perform(click());
        onView(withId(R.id.digit4)).perform(click());
        onView(withId(R.id.digit5)).perform(click());
        onView(withId(R.id.digit6)).perform(click());
        onView(withId(R.id.digit7)).perform(click());
        onView(withId(R.id.digit8)).perform(click());
        onView(withId(R.id.digit9)).perform(click());
        onView(withId(R.id.digit9)).perform(click());
        onView(withId(R.id.button_del)).perform(click());
        onView(withId(R.id.button_send)).perform(click());

        Field fieldCode = Activity.class.getDeclaredField("mResultCode");
        fieldCode.setAccessible(true);
        int mResultCode = fieldCode.getInt(mActivityRule.getActivity());
        assertTrue("The result code is not ok. ", mResultCode == Activity.RESULT_OK);

        Field f = Activity.class.getDeclaredField("mResultData");
        f.setAccessible(true);
        Intent mResultData = (Intent) f.get(mActivityRule.getActivity());
        assertTrue("The result data is not ok. ", mResultData.getStringExtra(PadActivity.RESULT_DIAL).equals("0123456789"));
    }

    @Test
    public void addDragAndDrop() throws Exception {
        onView(withId(R.id.digit1)).perform(click());

        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        Point placeholder = mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "custom_pad")).getChildren().get(0).getVisibleCenter();

        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "digit0")).drag(placeholder, 400);

        onView(withId(R.id.button_send)).perform(click());

        Field fieldCode = Activity.class.getDeclaredField("mResultCode");
        fieldCode.setAccessible(true);
        int mResultCode = fieldCode.getInt(mActivityRule.getActivity());
        assertTrue("The result code is not ok. ", mResultCode == Activity.RESULT_OK);

        Field f = Activity.class.getDeclaredField("mResultData");
        f.setAccessible(true);
        Intent mResultData = (Intent) f.get(mActivityRule.getActivity());
        assertTrue("The result data is not ok. ", mResultData.getStringExtra(PadActivity.RESULT_DIAL).equals("01"));
    }

    @Test
    public void delDragAndDrop() throws Exception {
        onView(withId(R.id.digit1)).perform(click());
        onView(withId(R.id.digit2)).perform(click());

        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        Point del = mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "frame_layout_delete")).getVisibleCenter();

        mDevice.findObject(By.res(BASIC_SAMPLE_PACKAGE, "custom_pad")).getChildren().get(1).drag(del, 400);

        onView(withId(R.id.button_send)).perform(click());

        Field fieldCode = Activity.class.getDeclaredField("mResultCode");
        fieldCode.setAccessible(true);
        int mResultCode = fieldCode.getInt(mActivityRule.getActivity());
        assertTrue("The result code is not ok. ", mResultCode == Activity.RESULT_OK);

        Field f = Activity.class.getDeclaredField("mResultData");
        f.setAccessible(true);
        Intent mResultData = (Intent) f.get(mActivityRule.getActivity());
        assertTrue("The result data is not ok. ", mResultData.getStringExtra(PadActivity.RESULT_DIAL).equals("2"));
    }
}
