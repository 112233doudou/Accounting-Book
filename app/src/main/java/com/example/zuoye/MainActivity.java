package com.example.zuoye;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zuoye.adapter.AccountAdapter;
import com.example.zuoye.db.AccountBean;
import com.example.zuoye.db.DBManager;
import com.example.zuoye.utils.BeiZhuDialog;
import com.example.zuoye.utils.BudgetDialog;
import com.example.zuoye.utils.MoreDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ListView todayLv;   //展示今日收支情况的listview
    ImageView searchIv;
    Button editBtn;
    ImageButton moreBtn;
//声明数据源
    List<AccountBean>mDatas;
    AccountAdapter adapter;
    int year, month, day;
    //头布局相关控件
    View headerView;
    TextView topOutTv, topInTv, topBudgetTv, topConTv;
    ImageView topShowIv;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTime();
        initView();
        preferences = getSharedPreferences("budget", Context.MODE_PRIVATE);
        //添加listview的头布局
        addLVHeaderView();
        mDatas = new ArrayList<>();
        //设置适配器：加载每一行数据到列表中
        adapter = new AccountAdapter(this,mDatas);
        todayLv.setAdapter(adapter);

    }
/**初始化自带的view的方法*/
    private void initView() {
        todayLv = findViewById(R.id.main_lv);
        editBtn = findViewById(R.id.main_btn_edit);
        moreBtn = findViewById(R.id.main_btn_more);
        searchIv = findViewById(R.id.main_iv_search);
        editBtn.setOnClickListener(this);
        moreBtn.setOnClickListener(this);
        searchIv.setOnClickListener(this);
        setLVLongClickListener();
    }
//设置listview的长按事件
    private void setLVLongClickListener() {
        todayLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0) {//点击了头布局
                    return false;
                }
                int pos = position-1;
                AccountBean clickBean = mDatas.get(pos);//获取正在被点击的这条信息

                //弹出提示用户是否删除的对话框
                showDeleteItemDialog(clickBean);
                return false;
            }
        });
    }
//弹出是否删除某一条记录的对话框
    private void showDeleteItemDialog( final AccountBean clickBean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("您确定要删除这条记录吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int click_id = clickBean.getId();
                        //执行删除操作
                        DBManager.deleteItemFromAccounttbById(click_id);
                        mDatas.remove(clickBean);//实时刷新，移除集合当中的对象
                        adapter.notifyDataSetChanged();//提示适配器更新数据
                        setTopTvShow();//改变头布局textview显示的内容
                    }
                });
        builder.create().show();//一定要显示对话框！不然无法展示
    }

    /*给listview添加头布局的方法*/
    private void addLVHeaderView() {
        //将布局转换成view对象
        headerView = getLayoutInflater().inflate(R.layout.item_mainlv_top, null);
        todayLv.addHeaderView(headerView);
        //查找头布局可用控件
        topOutTv = headerView.findViewById(R.id.item_mainlv_top_tv_out);
        topInTv = headerView.findViewById(R.id.item_mainlv_top_tv_in);
        topBudgetTv = headerView.findViewById(R.id.item_mainlv_top_tv_budget);
        topConTv = headerView.findViewById(R.id.item_mainlv_top_tv_day);
        topShowIv = headerView.findViewById(R.id.item_mainlv_top_iv_hide);

        topBudgetTv.setOnClickListener(this);
        headerView.setOnClickListener(this);
        topShowIv.setOnClickListener(this);

    }
/*获取今日的具体时间*/
    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    //表示当activity获取焦点时，会调用的方法
    @Override
    protected void onResume() {
        super.onResume();
        loadDBData();
        setTopTvShow();
    }
/*设置头布局当中文本内容的显示*/
    private void setTopTvShow() {
        //获取今日支出和收入总金额，显示在view当中
        float incomeOneDay = DBManager.getSumMoneyOneDay(year, month, day, 1);
        float outcomeOneDay = DBManager.getSumMoneyOneDay(year, month, day, 0);
        String infoOneDay = "今日支出 ￥"+outcomeOneDay+" 收入 ￥"+incomeOneDay;
        topConTv.setText(infoOneDay);
        //获取本月收入和支出总金额
        float incomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 1);
        float outcomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 0);
        topInTv.setText("￥"+incomeOneMonth);
        topOutTv.setText("￥"+outcomeOneMonth);
        //设置显示预算剩余
        float bmoney = preferences.getFloat("bmoney", 0);//预算
        if (bmoney==0) {
            topBudgetTv.setText("￥ 0");
        }else{
            float syMoney = bmoney-outcomeOneMonth;
            topBudgetTv.setText("￥ "+syMoney);
        }

    }
//加载数据库数据
    private void loadDBData() {
        List<AccountBean> list = DBManager.getAccountListOneDayFromAccounttb(year, month, day);
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_iv_search) {
            // 处理 main_iv_search 被点击的逻辑
            Intent it = new Intent(this,SearchActivity.class);//跳转界面
            startActivity(it);
        } else if (v.getId() == R.id.main_btn_edit) {
            // 处理 main_btn_edit 被点击的逻辑
            Intent it1 = new Intent(this,RecordActivity.class);//跳转界面
            startActivity(it1);
        } else if (v.getId() == R.id.main_btn_more) {
            // 处理 main_btn_more 被点击的逻辑
            MoreDialog moreDialog = new MoreDialog(this);
            moreDialog.show();
            moreDialog.setDialogSize();
        } else if (v.getId() == R.id.item_mainlv_top_tv_budget) {
            showBudgetDialog();

        } else if (v.getId() == R.id.item_mainlv_top_iv_hide) {
            //切换textview明文和密文
            toggleShow();

        } else if (v == headerView ) {
            //头布局被点击了
            Intent intent = new Intent();
            intent.setClass(this, MonthChartActivity.class);
            startActivity(intent);
        }


    }
//显示运算设置对话框
    private void showBudgetDialog() {
        BudgetDialog dialog = new BudgetDialog(this);
        dialog.show();
        dialog.setDialogSize();

        dialog.setOnEnsureListener(new BudgetDialog.OnEnsureListener() {
            @Override
            public void onEnsure(float money) {
                //将预算金额写入到共享参数当中进行存储
                SharedPreferences.Editor editor = preferences.edit();
                editor.putFloat("bmoney",money);
                editor.commit();

                //计算剩余金额
                float outcomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 0);
                float syMoney = money-outcomeOneMonth;//预算剩余 = 预算 - 支出
                topBudgetTv.setText("￥"+syMoney);
            }
        });
    }

    boolean isShow = true;
//点击头布局眼睛时，是明文就加密；是密文就显示
    private void toggleShow() {
        if (isShow) {
            //明文==》密文
            PasswordTransformationMethod passwordMethod = PasswordTransformationMethod.getInstance();
            topInTv.setTransformationMethod(passwordMethod);//设置隐藏
            topOutTv.setTransformationMethod(passwordMethod);//设置隐藏
            topBudgetTv.setTransformationMethod(passwordMethod);//设置隐藏
            topShowIv.setImageResource(R.mipmap.ih_hide);
            isShow = false;//设置标志位为隐藏状态
        }else {
            //密文==》明文
            HideReturnsTransformationMethod hideMethod = HideReturnsTransformationMethod.getInstance();
            topInTv.setTransformationMethod(hideMethod);//设置隐藏
            topOutTv.setTransformationMethod(hideMethod);//设置隐藏
            topBudgetTv.setTransformationMethod(hideMethod);//设置隐藏
            topShowIv.setImageResource(R.mipmap.ih_show);
            isShow = true;//设置标志位为隐藏状态
        }

    }
}