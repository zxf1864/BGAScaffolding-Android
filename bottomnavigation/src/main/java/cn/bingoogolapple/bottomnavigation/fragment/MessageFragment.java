package cn.bingoogolapple.bottomnavigation.fragment;

import android.os.Bundle;

import cn.bingoogolapple.bottomnavigation.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/7/3 下午8:29
 * 描述:
 */
public class MessageFragment extends BaseMainFragment {
    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_message);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        hiddenLeftCtv();
        setTitle(R.string.message);
        mTitlebar.setRightText(R.string.start_chat);

    }
}