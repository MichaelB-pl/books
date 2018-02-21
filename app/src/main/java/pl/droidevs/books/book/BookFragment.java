package pl.droidevs.books.book;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import pl.droidevs.books.R;
import pl.droidevs.books.savebook.SaveBookActivity;

import static pl.droidevs.books.book.BookActivity.EXTRAS_BOOK_ID;


/**
 * Created by micha on 21.02.2018.
 */

public class BookFragment extends Fragment {
    private static final int EDIT_BOOK_REQUEST_CODE = 205;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        AndroidInjection.inject(this);
        ButterKnife.bind(view);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id =item.getItemId();
        switch (id){
            case R.id.edit_book:
                Intent intent = new Intent(getActivity(), SaveBookActivity.class);
                intent.putExtra(SaveBookActivity.BOOK_ID_EXTRA, getActivity().getIntent().getSerializableExtra(EXTRAS_BOOK_ID));
                startActivityForResult(intent, EDIT_BOOK_REQUEST_CODE, ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity()).toBundle());
        }
        return super.onOptionsItemSelected(item);
    }


    //region Transitions
    private void setTransitionListener(@Nullable android.transition.Transition.TransitionListener newTransitionListener) {
        if (this.transitionListener != null) {
            getActivity().getWindow().getSharedElementEnterTransition().removeListener(this.transitionListener);
        }
        this.transitionListener = newTransitionListener;
        if (newTransitionListener != null) {
            getActivity().getWindow().getSharedElementEnterTransition().addListener(this.transitionListener);
        }
    }
    //endregion
}
