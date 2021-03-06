package com.qy.j4u.app.main.fragments;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qy.j4u.R;
import com.qy.j4u.base.BaseFragment;
import com.qy.j4u.global.User;
import com.qy.j4u.global.constants.TransferKeys;
import com.qy.j4u.model.entity.ITCategoryItem;
import com.qy.j4u.utils.ARouterWrapper;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class ITCategoryFragment extends BaseFragment {


    public static ITCategoryFragment newInstance() {
        ITCategoryFragment instance = new ITCategoryFragment();
        return instance;
    }

    @Override
    protected void daggerInject() {

    }

    @Override
    protected void initVariables(Bundle bundle) {

    }

    @Override
    protected void initView(View view) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity,
                RecyclerView.VERTICAL, false);

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_it_category;
    }

    @Override
    protected void loadData() {
        BaseQuickAdapter<ITCategoryItem, BaseViewHolder> adapter =
                new BaseQuickAdapter<ITCategoryItem, BaseViewHolder>(R.layout.item_inventroy,
                        User.getUser().getIt_categories()) {
                    @Override
                    protected void convert(BaseViewHolder helper, ITCategoryItem item) {
                        helper.setText(R.id.tv_item_desc, item.getName());
                    }
                };

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ARouterWrapper.build(ARouterWrapper.Route.ESSAY_LIST)
                        .withInt(TransferKeys.CATEGORY_ID,User.getUser().getIt_categories().get(position).getId())
                        .navigation(mActivity);
            }
        });
    }
}
