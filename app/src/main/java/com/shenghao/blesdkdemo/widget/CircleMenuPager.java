package com.shenghao.blesdkdemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.shenghao.blesdkdemo.R;

import java.util.ArrayList;
import java.util.List;

public class CircleMenuPager extends LinearLayout {

    private ViewPager2 viewPager;
    private LinearLayout indicatorLayout;
    private MenuPagerAdapter adapter;
    private List<MenuItem> menuItems = new ArrayList<>();
    private OnMenuClickListener onMenuClickListener;
    private int itemsPerPage = 4;
    private int selectedPosition;

    public interface OnMenuClickListener {
        void onMenuClick(String tag, MenuItem item);
    }

    public CircleMenuPager(Context context) {
        this(context, null);
    }

    public CircleMenuPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleMenuPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.layout_circle_menu_pager, this, true);

        viewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);

        setupViewPager();
    }

    private void setupViewPager() {
        adapter = new MenuPagerAdapter();
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateIndicator(position);
            }
        });
    }

    public void setMenuData(List<MenuItem> items) {
        this.menuItems.clear();
        this.menuItems.addAll(items);

        adapter.notifyDataSetChanged();
        setupIndicators();
        updateIndicator(selectedPosition);
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        this.onMenuClickListener = listener;
    }

    // 根据位置更新菜单项
    public void updateMenuItem(int position, int iconResId, String label, Integer textColor) {
        if (position >= 0 && position < menuItems.size()) {
            MenuItem item = menuItems.get(position);
            item.iconResId = iconResId;
            item.label = label;
            if (textColor != null) {
                item.textColor = textColor;
            }

            // 通知适配器更新
            adapter.notifyItemChanged(position / itemsPerPage);
        }
    }

    // 根据标签更新菜单项
    public void updateMenuItemByTag(String tag, int iconResId, String label, Integer textColor) {
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem item = menuItems.get(i);
            if (tag.equals(item.tag)) {
                updateMenuItem(i, iconResId, label, textColor);
                break;
            }
        }
    }

    // 根据标签移除菜单项
    public void removeMenuItemByTag(String tag) {
        for (int i = menuItems.size() - 1; i >= 0; i--) {
            MenuItem item = menuItems.get(i);
            if (tag.equals(item.tag)) {
                removeMenuItem(i);
                break;
            }
        }
    }

    // 根据位置移除菜单项
    public void removeMenuItem(int position) {
        if (position >= 0 && position < menuItems.size()) {
            menuItems.remove(position);
            refreshMenuData();
        }
    }

    // 添加菜单项
    public void addMenuItem(MenuItem item) {
        menuItems.add(item);
        refreshMenuData();
    }

    // 在指定位置添加菜单项
    public void addMenuItem(int position, MenuItem item) {
        if (position >= 0 && position <= menuItems.size()) {
            if(menuItems.get(position).tag.equals(item.tag))
                return;
            menuItems.add(position, item);
            refreshMenuData();
        }
    }

    // 根据标签获取菜单项位置
    public int getMenuItemPositionByTag(String tag) {
        for (int i = 0; i < menuItems.size(); i++) {
            if (tag.equals(menuItems.get(i).tag)) {
                return i;
            }
        }
        return -1;
    }

    // 根据标签获取菜单项
    public MenuItem getMenuItemByTag(String tag) {
        for (MenuItem item : menuItems) {
            if (tag.equals(item.tag)) {
                return item;
            }
        }
        return null;
    }

    // 显示/隐藏菜单项
    public void setMenuItemVisible(String tag, boolean visible) {
        MenuItem item = getMenuItemByTag(tag);
        if (item != null) {
            item.visible = visible;
            refreshMenuData();
        }
    }

    // 启用/禁用菜单项
    public void setMenuItemEnabled(String tag, boolean enabled) {
        MenuItem item = getMenuItemByTag(tag);
        if (item != null) {
            item.enabled = enabled;
            refreshMenuData();
        }
    }

    // 获取所有可见的菜单项
    public List<MenuItem> getVisibleMenuItems() {
        List<MenuItem> visibleItems = new ArrayList<>();
        for (MenuItem item : menuItems) {
            if (item.visible) {
                visibleItems.add(item);
            }
        }
        return visibleItems;
    }

    // 批量更新多个菜单项
    public void updateMenuItems(List<MenuUpdate> updates) {
        for (MenuUpdate update : updates) {
            if (update.position >= 0 && update.position < menuItems.size()) {
                updateMenuItem(update.position, update.iconResId, update.label, update.textColor);
            }
        }
    }

    // 刷新菜单数据
    private void refreshMenuData() {
        adapter.notifyDataSetChanged();
        setupIndicators();
        updateIndicator(viewPager.getCurrentItem());
    }

    private void setupIndicators() {
        indicatorLayout.removeAllViews();

        int pageCount = getPageCount();

        if (pageCount <= 1) {
            indicatorLayout.setVisibility(View.GONE);
            return;
        }

        indicatorLayout.setVisibility(View.VISIBLE);

        for (int i = 0; i < pageCount; i++) {
            View indicator = createIndicator(i == 0);
            indicatorLayout.addView(indicator);
        }
    }

    private View createIndicator(boolean isSelected) {
        View indicator = new View(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                dp2px(16), dp2px(3)
        );
        params.setMargins(dp2px(2), 0, dp2px(2), 0);
        indicator.setLayoutParams(params);
        indicator.setBackgroundResource(isSelected ?
                R.drawable.indicator_selected : R.drawable.indicator_normal);
        return indicator;
    }

    private void updateIndicator(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        int pageCount = getPageCount();

        if (pageCount <= 1 || indicatorLayout.getChildCount() == 0) {
            return;
        }

        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            View indicator = indicatorLayout.getChildAt(i);
            boolean isSelected = (i == selectedPosition);
            indicator.setBackgroundResource(isSelected ?
                    R.drawable.indicator_selected : R.drawable.indicator_normal);
        }
    }

    private int getPageCount() {
        if (menuItems.isEmpty()) return 0;
        return (int) Math.ceil((double) menuItems.size() / itemsPerPage);
    }

    private class MenuPagerAdapter extends RecyclerView.Adapter<MenuPagerAdapter.PageViewHolder> {

        @NonNull
        @Override
        public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_page_menu, parent, false);
            return new PageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
            holder.bindPage(position);
        }

        @Override
        public int getItemCount() {
            return getPageCount();
        }

        class PageViewHolder extends RecyclerView.ViewHolder {
            private LinearLayout menuContainer;

            PageViewHolder(@NonNull View itemView) {
                super(itemView);
                menuContainer = itemView.findViewById(R.id.menuContainer);
            }

            void bindPage(int pageIndex) {
                menuContainer.removeAllViews();

                int startIndex = pageIndex * itemsPerPage;
                int endIndex = Math.min(startIndex + itemsPerPage, menuItems.size());

                for (int i = startIndex; i < endIndex; i++) {
                    MenuItem item = menuItems.get(i);
                    // 只显示可见的菜单项
                    if (item.visible) {
                        View menuView = createMenuItemView(item, i);
                        menuContainer.addView(menuView);
                    } else {
                        // 如果不可见，添加空白View占位
                        View emptyView = new View(getContext());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
                        );
                        emptyView.setLayoutParams(params);
                        menuContainer.addView(emptyView);
                    }
                }

                // 补充空白View以确保布局正确
                int displayedItems = 0;
                for (int i = startIndex; i < endIndex; i++) {
                    if (menuItems.get(i).visible) {
                        displayedItems++;
                    }
                }

                int remainingItems = itemsPerPage - displayedItems;
                for (int i = 0; i < remainingItems; i++) {
                    View emptyView = new View(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
                    );
                    emptyView.setLayoutParams(params);
                    menuContainer.addView(emptyView);
                }
            }

            private View createMenuItemView(final MenuItem item, final int position) {
                View menuView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_circle_menu, menuContainer, false);

                ImageForegroundImageView iconView = menuView.findViewById(R.id.iv_icon);
                TextView labelView = menuView.findViewById(R.id.tv_label);

                // 设置图标
//                iconView.setBackgroundResource(item.iconResId);
                // 设置背景图片（主内容）
                iconView.setBackgroundImageResource(item.iconResId);
                // 设置前景圆环图片
                if(item.iconFgResId != null)
                    iconView.setForegroundImageResource(item.iconFgResId);
                // 设置前景图片的缩放类型
//                iconView.getForegroundImageView().setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                // 设置背景图片的缩放类型
//                iconView.getBackgroundImageView().setScaleType(ImageView.ScaleType.CENTER_CROP);
                // 设置文字
                labelView.setText(item.label);

                // 设置文字颜色（如果有）
                if (item.textColor != 0) {
                    labelView.setTextColor(item.textColor);
                }

                // 设置图标颜色（如果有）
                if (item.iconTint != 0) {
                    iconView.getBackgroundImageView().setColorFilter(item.iconTint);
                }

                // 设置是否启用
                menuView.setEnabled(item.enabled);
                if (!item.enabled) {
                    menuView.setAlpha(0.5f);
                } else {
                    menuView.setAlpha(1.0f);
                }

                menuView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onMenuClickListener != null && item.enabled) {
                            if(item.iconFgResId != null){
                                if(iconView.isAnimating()) return;
                                iconView.startRotation();
                            }
                            onMenuClickListener.onMenuClick(item.tag.toString(), item);

                        }
                    }
                });

                return menuView;
            }
        }
    }

    // 增强的菜单项数据类
    public static class MenuItem {
        private Integer iconFgResId;//前景圈圈
        public int iconResId;
        public String label;
        public String tag; // 改为String类型，便于识别
        public int textColor = 0;
        public int iconTint = 0;
        public boolean visible = true; // 是否可见
        public boolean enabled = true; // 是否启用

        public MenuItem(int iconResId, @Nullable Integer iconFgResId, String label, String tag) {
            this.iconResId = iconResId;
            this.iconFgResId = iconFgResId;
            this.label = label;
            this.tag = tag;
        }

        public MenuItem(int iconResId, String label, String tag, int textColor) {
            this.iconResId = iconResId;
            this.label = label;
            this.tag = tag;
            this.textColor = textColor;
        }
    }

    // 菜单更新数据类
    public static class MenuUpdate {
        public int position;
        public int iconResId;
        public String label;
        public Integer textColor;

        public MenuUpdate(int position, int iconResId, String label) {
            this.position = position;
            this.iconResId = iconResId;
            this.label = label;
        }

        public MenuUpdate(int position, int iconResId, String label, Integer textColor) {
            this.position = position;
            this.iconResId = iconResId;
            this.label = label;
            this.textColor = textColor;
        }
    }

    private int dp2px(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }
}