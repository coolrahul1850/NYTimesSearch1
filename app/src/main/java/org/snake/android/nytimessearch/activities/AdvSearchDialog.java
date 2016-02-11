package org.snake.android.nytimessearch.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.snake.android.nytimessearch.R;

/**
 * Created by rmukhedkar on 2/10/16.
 */
public class AdvSearchDialog extends DialogFragment implements View.OnClickListener{


   // @Bind (R.id.adv_sort_order) Spinner advSortOrder;
   // @Bind (R.id.advBgnDate) DateUtils advBgnDate;

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
        Button advBtnSave = (Button) view.findViewById(R.id.advbtnSave);
        advBtnSave.setOnClickListener(this);
        String title = getArguments().getString("Advanced Search", "Advanced Search");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field

        //Filling the drop down for order
        Spinner advSortOrder = (Spinner) view.findViewById(R.id.adv_sort_order);
        String [] sortOrder = new String[] {"oldest","newest"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item, sortOrder);
        advSortOrder.setAdapter(sortAdapter);

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }



    // @Bind (R.id.etQuery) EditText etQuery;
    @Override
    public void onClick(View v) {
        Log.d("Clicked","Clicked");
        EditNameDialogListener listener = (EditNameDialogListener) getActivity();
        listener.onFinishEditDialog("01012016");

        dismiss();

    }
}
