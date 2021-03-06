/*******************************************************************************
 * Mirakel is an Android App for managing your ToDo-Lists
 *
 *  Copyright (c) 2013-2014 Anatolij Zelenin, Georg Semmler.
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package de.azapps.mirakel.new_ui.fragments;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faizmalkani.floatingactionbutton.FloatingActionButton;

import de.azapps.mirakel.model.list.ListMirakel;
import de.azapps.mirakel.model.semantic.Semantic;
import de.azapps.mirakel.model.task.Task;
import de.azapps.mirakel.new_ui.R;
import de.azapps.mirakel.new_ui.adapter.TaskAdapter;
import de.azapps.mirakel.new_ui.interfaces.OnTaskSelectedListener;
import de.azapps.tools.Log;

import static com.google.common.base.Optional.fromNullable;

public class TasksFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    public static final String ARGUMENT_LIST = "list";
    private static final String TAG = "de.azapps.mirakel.new_ui.fragments.TasksFragment";

    private TaskAdapter mAdapter;
    private RecyclerView mListView;
    private View layout;
    private OnTaskSelectedListener mListener;

    private ListMirakel listMirakel;

    public TasksFragment() {
        // Required empty public constructor
    }

    public static TasksFragment newInstance(final ListMirakel listMirakel) {
        final TasksFragment f = new TasksFragment();
        // Supply num input as an argument.
        final Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_LIST, listMirakel);
        f.setArguments(args);
        return f;
    }

    public ListMirakel getList() {
        return listMirakel;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new TaskAdapter(getActivity(), null, 0, mListener);
        mListView.setAdapter(mAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTaskSelectedListener) activity;
        } catch (final ClassCastException e) {
            Log.e(TAG, activity.toString() + " must implement OnArticleSelectedListener", e);
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.fragment_tasks, container, false);
        mListView = (RecyclerView) layout.findViewById(R.id.task_listview);
        initFab();
        return layout;
    }

    public void initFab() {
        final FloatingActionButton mFab = (FloatingActionButton) layout.findViewById(R.id.fabbutton);
        mFab.setColor(getResources().getColor(R.color.colorAccent));
        mFab.setDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));
        mFab.hide(false);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFAB(v);
            }
        });
    }

    private void clickFAB(View v) {
        final Task task = Semantic.createStubTask(getString(R.string.task_new), fromNullable(listMirakel),
                          true,
                          getActivity());
        mListener.onTaskSelected(task);
    }

    public void setList(final ListMirakel listMirakel) {
        this.listMirakel = listMirakel;
        final Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_LIST, listMirakel);
        getLoaderManager().restartLoader(0, args, this);
    }

    @Override
    public Loader onCreateLoader(final int i, final Bundle arguments) {
        listMirakel = arguments.getParcelable(ARGUMENT_LIST);
        return listMirakel.getTasksSupportCursorLoader();
    }

    @Override
    public void onLoadFinished(final Loader loader, final Object o) {
        mAdapter.swapCursor((Cursor) o);
    }

    @Override
    public void onLoaderReset(final Loader loader) {
        mAdapter.swapCursor(null);
    }
}
