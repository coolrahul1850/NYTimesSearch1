package org.snake.android.nytimessearch.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import org.snake.android.nytimessearch.R;

/**
 * Created by rmukhedkar on 2/10/16.
 */
public class AdvSearchDialog extends DialogFragment implements View.OnClickListener{

    //To retain the value of the advanced search
    public static String preAdvBgnDate;



    Button advBtnSave;
    EditText advBgnDate;
    CheckBox arts;
    CheckBox fashion;
    CheckBox sports;
    Spinner advSortOrder;
    String advsortorder;


    public AdvSearchDialog()
    {

    }

    public static AdvSearchDialog newInstance (String title)
    {
        AdvSearchDialog frag = new AdvSearchDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //  mEditText.setOnEditorActionListener(this);


        return inflater.inflate(R.layout.item_advanced_search_dialog, container);
    }

    public interface EditNameDialogListener {
        void onFinishEditDialog(String inputText);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        advBtnSave = (Button) view.findViewById(R.id.advbtnSave);
        advBgnDate = (EditText) view.findViewById(R.id.advBgnDate);
        advBgnDate.setText(preAdvBgnDate);
        arts = (CheckBox) view.findViewById(R.id.cb_arts);
        fashion = (CheckBox) view.findViewById(R.id.ck_fashion);
        sports = (CheckBox) view.findViewById(R.id.ck_sports);


        advBtnSave.setOnClickListener(this);
        String title = getArguments().getString("Advanced Search", "Advanced Search");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field

        //Filling the drop down for order
        advSortOrder = (Spinner) view.findViewById(R.id.adv_sort_order);
        String [] sortOrder = new String[] {"oldest","newest"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item, sortOrder);
        advSortOrder.setAdapter(sortAdapter);

        advSortOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                advsortorder = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }



    // @Bind (R.id.etQuery) EditText etQuery;
    @Override
    public void onClick(View v) {

        preAdvBgnDate = advBgnDate.getText().toString();
        EditNameDialogListener advBgnDateListener = (EditNameDialogListener) getActivity();
        if (preAdvBgnDate != "")
        {
            advBgnDateListener.onFinishEditDialog(advBgnDate.getText().toString()+"this"+advsortorder);
        }
        if (preAdvBgnDate == "")
        {
            advBgnDateListener.onFinishEditDialog(advsortorder);
        }
        dismiss();
    }
}
