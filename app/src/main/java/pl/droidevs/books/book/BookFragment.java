package pl.droidevs.books.book;

import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import pl.droidevs.books.R;
import pl.droidevs.books.model.Book;
import pl.droidevs.books.savebook.SaveBookActivity;

import static pl.droidevs.books.apphelper.ColorHelper.getActionBarColorFromSwatch;
import static pl.droidevs.books.apphelper.ColorHelper.getDominantColor;
import static pl.droidevs.books.apphelper.ColorHelper.getStatusBarColorFromSwatch;


/**
 * Created by micha on 21.02.2018.
 */

public class BookFragment extends Fragment {
    private static final String EXTRAS_POSITION = "EXTRAS_POSITION";
    public static final String BUNDLE_TRANSITION_EXTRAS = "BUNDLE_TRANSITION_EXTRAS";

    private static final int EDIT_BOOK_REQUEST_CODE = 205;

    public final String TAG;

    //region AppBarLayoutOffset
    private static final double APP_BAR_MAX_COLLEPSED_SCROLL_PERCENT_VALUE = 0.4;
    private static final double APP_BAR_MIN_EXPANDED_SCROLL_PERCENT_VALUE = 1.0 - APP_BAR_MAX_COLLEPSED_SCROLL_PERCENT_VALUE;
    private static final double APP_BAR_ALPHA_SCROLL_MAX_PERCENT_VALUE = APP_BAR_MIN_EXPANDED_SCROLL_PERCENT_VALUE - APP_BAR_MAX_COLLEPSED_SCROLL_PERCENT_VALUE;

    private static final int MAX_ALPHA = 255;
    private static final int MIN_ALPHA = 0;

    private int lastAppBarOffset = 1;
    private int linesCount = 0;
    private double appBarScrollPercentValue;
    //endregion

    private BookViewModel viewModel;

    private android.transition.Transition.TransitionListener transitionListener;

    private int statusBarColor;

    private int position;

    //region Butter binding
    @BindView(R.id.album_iv)
    ImageView imageView;

    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    @BindView(R.id.shadow_view)
    View shadowView;

    @BindView(R.id.tv_expanded_title)
    TextView tvExpandedTitle;

    @BindView(R.id.tv_collapsed_title)
    TextView tvCollpsedTitle;

    @BindView(R.id.cl_title)
    ConstraintLayout clTitle;

    @BindView(R.id.author_tv)
    TextView authorTextView;

    @BindView(R.id.category_tv)
    TextView categoryTextView;

    @BindView(R.id.description_tv)
    TextView descriptionTextView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    //endregion

    public static BookFragment newInstance(int position) {
        BookFragment fragment = new BookFragment(position);
        Bundle args = new Bundle();
        args.putInt(EXTRAS_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    public BookFragment() {
        TAG = "index_";
    }

    private BookFragment(int position) {
        TAG = "index_" + position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container, false);
        setupBinding(view);
        setupAppBarLayoutOffsetListener();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(BookViewModel.class);
        if (savedInstanceState != null) {

        } else {
            position = getArguments().getInt(EXTRAS_POSITION);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setTransitionListener(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.book_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.edit_book:
                Intent intent = new Intent(getActivity(), SaveBookActivity.class);
                /*intent.putExtra(SaveBookActivity.BOOK_ID_EXTRA, getActivity().getIntent().getSerializableExtra(EXTRAS_BOOK_ID));
                startActivityForResult(intent, EDIT_BOOK_REQUEST_CODE, ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity()).toBundle());*/
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupBinding(View view) {
        ButterKnife.bind(this, view);
        BookActivity bookActivity = (BookActivity) getActivity();
        Book book = bookActivity.getBook(position);
        if (book != null) {
            tvCollpsedTitle.setText(book.getTitle());
            tvExpandedTitle.setText(book.getTitle());
            collapsingToolbarLayout.setTitle(/*book.getTitle()*/" ");
            authorTextView.setText(book.getAuthor());
            categoryTextView.setText(book.getCategory().toString());
            descriptionTextView.setText(book.getDescription());

            if (book.getImageUrl() != null) {
                loadCover(book.getImageUrl());
            }


            imageView.setTransitionName("image_" + position);
            tvExpandedTitle.setTransitionName("title_" + position);
            authorTextView.setTransitionName("author_" + position);
        }
    }


    private void loadCover(String imageUrl) {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        super.onResourceReady(resource, transition);

                        setupBarColors(resource);
                    }
                });
    }

    private void setupBarColors(Bitmap bitmap) {
        Palette.Swatch swatch = getDominantColor(bitmap);

        if (swatch != null) {
            collapsingToolbarLayout.setContentScrimColor(getActionBarColorFromSwatch(swatch));
            statusBarColor = getStatusBarColorFromSwatch(swatch);
        }
    }

    public int getStatusBarColor() {
        return statusBarColor;
    }


    public void addAnimationBundle(Intent intent) {
        Bundle animationBundle = new Bundle();
        /*animationBundle.putString(BookActivity.EXTRAS_IMAGE_TRANSITION_NAME, view.findViewById(R.id.iv_book).getTransitionName());
        animationBundle.putString(EXTRAS_TITLE_TRANSITION_NAME, view.findViewById(R.id.tv_book_title).getTransitionName());
        animationBundle.putString(EXTRAS_AUTHOR_TRANSITION_NAME, view.findViewById(R.id.tv_book_author).getTransitionName());*/
    }

    private void setupAppBarLayoutOffsetListener() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int linesCount = tvExpandedTitle.getLineCount();
            if (this.lastAppBarOffset != verticalOffset || linesCount != this.linesCount) {
                this.lastAppBarOffset = verticalOffset;
                this.linesCount = linesCount;

                int appBarScrollRange = appBarLayout.getTotalScrollRange();
                appBarScrollPercentValue = (double) (appBarScrollRange + verticalOffset) / (double) appBarScrollRange;

                float collapsedTextSize = getResources().getDimension(R.dimen.collapsed_toolbar_text_size);
                float expandTextSize = getResources().getDimension(R.dimen.expanded_toolbar_text_size);
                float textSizeDifference = expandTextSize - collapsedTextSize;
                float textSize = collapsedTextSize + (float) (textSizeDifference * appBarScrollPercentValue);
                tvExpandedTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                tvCollpsedTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);


                int expandedAlpha;
                int collapsedAlpha;
                if (appBarScrollPercentValue > APP_BAR_MIN_EXPANDED_SCROLL_PERCENT_VALUE) {
                    expandedAlpha = MAX_ALPHA;
                    collapsedAlpha = MIN_ALPHA;
                } else if (appBarScrollPercentValue < APP_BAR_MAX_COLLEPSED_SCROLL_PERCENT_VALUE) {
                    expandedAlpha = MIN_ALPHA;
                    collapsedAlpha = MAX_ALPHA;
                } else {
                    double aplhaPercent = (appBarScrollPercentValue - APP_BAR_MAX_COLLEPSED_SCROLL_PERCENT_VALUE) / APP_BAR_ALPHA_SCROLL_MAX_PERCENT_VALUE;
                    expandedAlpha = (int) ((double) MAX_ALPHA * aplhaPercent);
                    collapsedAlpha = (int) ((double) MAX_ALPHA * (1.0 - aplhaPercent));
                }
                tvExpandedTitle.setTextColor(ColorUtils.setAlphaComponent(Color.WHITE, expandedAlpha));
                tvCollpsedTitle.setTextColor(ColorUtils.setAlphaComponent(Color.WHITE, collapsedAlpha));


                int bottomCollapsedSpacing = getResources().getDimensionPixelSize(R.dimen.collapsed_toolbar_bottom_spacing);
                int bottomExpandedSpacing = getResources().getDimensionPixelSize(R.dimen.expanded_toolbar_bottom_spacing);
                int bottomSpacingDifference = bottomExpandedSpacing - bottomCollapsedSpacing;
                int bottomSpacing = bottomCollapsedSpacing + (int) (bottomSpacingDifference * appBarScrollPercentValue);
                ViewGroup.MarginLayoutParams clLayoutParams = (ViewGroup.MarginLayoutParams) clTitle.getLayoutParams();
                if (linesCount > 1) {
                    int lineHeight = tvExpandedTitle.getLineHeight();
                    double lineDistance = lineHeight * (linesCount - 1);
                    int titleNegativeSpacing = (int) (lineDistance * (1.0 - appBarScrollPercentValue));

                    clLayoutParams.bottomMargin = -titleNegativeSpacing;
                } else {
                    clLayoutParams.bottomMargin = 0;
                }
                clTitle.requestLayout();

                int endCollapsedSpacing = getResources().getDimensionPixelSize(R.dimen.toolbar_item_size);
                int horizontalExpandedSpacing = getResources().getDimensionPixelSize(R.dimen.spacing_normal);


                double endSpacingDifference = /*horizontalExpandedSpacing +*/ endCollapsedSpacing;
                int endSpacing = /*endCollapsedSpacing*/horizontalExpandedSpacing + (int) (endSpacingDifference * (1.0 - appBarScrollPercentValue));


                ViewGroup.MarginLayoutParams tvExpandedTitleLayoutParams = (ViewGroup.MarginLayoutParams) tvExpandedTitle.getLayoutParams();
                tvExpandedTitleLayoutParams.bottomMargin = bottomSpacing;

                /*
                * "layoutParams.setMarginEnd(endSpacing);"
                * is not working :(
                */
                boolean isRTL = getResources().getBoolean(R.bool.is_layout_direction_rtl);
                if (isRTL) {
                    tvExpandedTitleLayoutParams.leftMargin = endSpacing;
                } else {
                    tvExpandedTitleLayoutParams.rightMargin = endSpacing;
                }

                tvExpandedTitle.requestLayout();
            }
        });
    }


    //region Transitions
    public void setEnterTransition() {
        setTransitionListener(getEnterTransitionListener());
    }

    public void setExitTransition() {
        if (appBarScrollPercentValue < APP_BAR_MAX_COLLEPSED_SCROLL_PERCENT_VALUE) {
            tvExpandedTitle.setTransitionName("");
            tvCollpsedTitle.setTransitionName("title_" + position);

            int ivHeight = imageView.getHeight();
            int tHeight = toolbar.getHeight();
            int ivPadding = -((ivHeight - tHeight) / 2);
            imageView.setTranslationY(ivPadding);

            setTransitionListener(getExitTransitionListener(tvCollpsedTitle));
        } else {
            tvCollpsedTitle.setTransitionName("");
            tvExpandedTitle.setTransitionName("title_" + position);
            setTransitionListener(getExitTransitionListener(tvExpandedTitle));
        }
    }


    private void setTransitionListener(@Nullable android.transition.Transition.TransitionListener newTransitionListener) {
        if (this.transitionListener != null) {
            getActivity().getWindow().getSharedElementEnterTransition().removeListener(this.transitionListener);
        }
        this.transitionListener = newTransitionListener;
        if (newTransitionListener != null) {
            getActivity().getWindow().getSharedElementEnterTransition().addListener(this.transitionListener);
        }
    }

    private android.transition.Transition.TransitionListener getEnterTransitionListener() {
        return new android.transition.Transition.TransitionListener() {
            private float detailsTitleTextSize = -1;
            private float detailsAuthorTextSize = -1;

            @Override
            public void onTransitionStart(android.transition.Transition transition) {
                detailsAuthorTextSize = authorTextView.getTextSize();
                if (detailsAuthorTextSize >= 0) {
                    authorTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, viewModel.masterAuthorTextSize);
                    ValueAnimator animator = ValueAnimator.ofFloat(viewModel.masterAuthorTextSize, detailsAuthorTextSize);
                    animator.setDuration(250);
                    animator.addUpdateListener(valueAnimator -> {
                        float textSize = (float) valueAnimator.getAnimatedValue();
                        authorTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    });
                    animator.start();
                }

                detailsTitleTextSize = tvExpandedTitle.getTextSize();
                if (detailsTitleTextSize >= 0) {
                    tvExpandedTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, viewModel.masterTitleTextSize);
                    ValueAnimator animator = ValueAnimator.ofFloat(viewModel.masterTitleTextSize, detailsTitleTextSize);
                    animator.setDuration(250);
                    animator.addUpdateListener(valueAnimator -> {
                        float textSize = (float) valueAnimator.getAnimatedValue();
                        tvExpandedTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    });
                    animator.start();
                }
                ValueAnimator titleColorAnimator = ValueAnimator.ofArgb(R.color.defaultTextViewTextColor, Color.WHITE);
                titleColorAnimator.setDuration(250);
                titleColorAnimator.addUpdateListener(valueAnimator -> {
                    int color = (int) valueAnimator.getAnimatedValue();
                    tvExpandedTitle.setTextColor(color);
                });
                titleColorAnimator.start();
            }

            @Override
            public void onTransitionEnd(android.transition.Transition transition) {
                if (detailsAuthorTextSize >= 0) {
                    authorTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, detailsAuthorTextSize);
                }
                if (detailsTitleTextSize >= 0) {
                    tvExpandedTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, detailsTitleTextSize);
                }
            }

            @Override
            public void onTransitionCancel(android.transition.Transition transition) {

            }

            @Override
            public void onTransitionPause(android.transition.Transition transition) {

            }

            @Override
            public void onTransitionResume(android.transition.Transition transition) {

            }
        };
    }

    private android.transition.Transition.TransitionListener getExitTransitionListener(TextView tvTitle) {
        return new android.transition.Transition.TransitionListener() {
            private float detailsTitleTextSize = -1;
            private float detailsAuthorTextSize = -1;


            @Override
            public void onTransitionStart(android.transition.Transition transition) {
                detailsAuthorTextSize = authorTextView.getTextSize();
                if (detailsAuthorTextSize >= 0) {
                    authorTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, detailsAuthorTextSize);
                    ValueAnimator animator = ValueAnimator.ofFloat(detailsAuthorTextSize, viewModel.masterAuthorTextSize);
                    animator.setDuration(250);
                    animator.addUpdateListener(valueAnimator -> {
                        float textSize = (float) valueAnimator.getAnimatedValue();
                        authorTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    });
                    animator.start();
                }

                detailsTitleTextSize = tvTitle.getTextSize();
                if (detailsTitleTextSize >= 0) {
                    tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, detailsTitleTextSize);
                    ValueAnimator animator = ValueAnimator.ofFloat(detailsTitleTextSize, viewModel.masterTitleTextSize);
                    animator.setDuration(250);
                    animator.addUpdateListener(valueAnimator -> {
                        float textSize = (float) valueAnimator.getAnimatedValue();
                        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                    });
                    animator.start();
                }
                int defColor;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    defColor = getResources().getColor(R.color.defaultTextViewTextColor, getActivity().getTheme());
                } else {
                    defColor = getResources().getColor(R.color.defaultTextViewTextColor);
                }
                ValueAnimator titleColorAnimator = ValueAnimator.ofArgb(Color.WHITE, defColor);
                titleColorAnimator.setDuration(250);
                titleColorAnimator.addUpdateListener(valueAnimator -> {
                    int color = (int) valueAnimator.getAnimatedValue();
                    tvTitle.setTextColor(color);
                });
                titleColorAnimator.start();
            }

            @Override
            public void onTransitionEnd(android.transition.Transition transition) {
                if (detailsAuthorTextSize >= 0) {
                    authorTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, viewModel.masterAuthorTextSize);
                }
                if (detailsTitleTextSize >= 0) {
                    tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, viewModel.masterTitleTextSize);
                }
            }

            @Override
            public void onTransitionCancel(android.transition.Transition transition) {

            }

            @Override
            public void onTransitionPause(android.transition.Transition transition) {

            }

            @Override
            public void onTransitionResume(android.transition.Transition transition) {

            }
        };
    }
    //endregion
}
