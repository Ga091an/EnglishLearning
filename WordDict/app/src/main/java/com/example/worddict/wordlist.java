package com.example.worddict;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.worddict.model.Eword;
import com.example.worddict.sql.SqlHelper;
import com.example.worddict.sql.WordDAO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class wordlist extends AppCompatActivity implements View.OnClickListener  {
    private boolean isSelectedAll = true;//用来控制点击全选，全选和全不选相互切换
    private List<String> mCheckedData = new ArrayList<>();
    private ListView lvData;
    private LinearLayout mLlEditBar;//控制下方那一行的显示与隐藏
    private MyAdapter adapter;
    private WordDAO wDao;
    private List<Eword> mEwordList;
    private List<String> mData = new ArrayList<>();
    private Context context = this;
    private ListView mListView;
    private SparseBooleanArray stateCheckedMap = new SparseBooleanArray();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wordlist);
        initView();
        initData();
        init();
        wDao = new WordDAO(context);
        //wDao.dataSyncMtoT();

        mEwordList = wDao.Queryword(-1);


        mData = getData();
        // wDao.Insertword("nice");
        //mListviewItemList = getData();
        //mData=getData();
        //mTextView = (TextView) findViewById(R.id.PtView);
        //mTextView.setText(wDao.CountDo());

//        mListView = (ListView) findViewById(R.id.wordlist);
        System.out.println(mData.size());
//       size ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                wordlist.this, android.R.layout.simple_list_item_1, mData);
//        ((ListView) findViewById(R.id.wordlist)).setAdapter(adapter);
        //adapter = new ListviewItemAdapter(this, mListviewItemList);




    mListView = (ListView) findViewById(R.id.lv);
    //adapter = new ListviewItemAdapter(this, mListviewItemList);
    adapter = new MyAdapter(wordlist.this,mData,stateCheckedMap);
        mListView.setAdapter(adapter);
    setOnListViewItemClickListener();
    setOnListViewItemLongClickListener();
        System.out.println("adapter init");

}

    // Here is setting ChexBox
//        mCheckBox = (CheckBox) findViewById(R.id.checkBox1);
//        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // TODO Auto-generated method stub
//                if (isChecked == true) {
//                    // Toast.makeText(getApplicationContext(), "All Words", Toast.LENGTH_SHORT).show();
//                    mEwordList = wDao.Queryword(-1);
//
//                    mListviewItemList.clear();
//                    mListviewItemList.addAll(getData());
//                    for (int i = 0; i < mListviewItemList.size(); i++) {
//                        if (1 == mEwordList.get(i).getIsOK())
//                            mListviewItemList.get(i).isdeletedo();
//                    }
//                    adapter.notifyDataSetChanged();
//                }
//                if (isChecked == false) {
//                    mEwordList = wDao.Queryword(0);
//                    mListviewItemList.clear();
//                    mListviewItemList.addAll(getData());
//                    adapter.notifyDataSetChanged();
//                    // Toast.makeText(getApplicationContext(), "New Words", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    // ///////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_cart://监听菜单按钮
                //Intent intent=new Intent();
                Log.d("onOptionsItemSelected",String.valueOf(mData.size()));
                //intent.putStringArrayListExtra("word",(ArrayList<String>)mData);
                //setResult(RESULT_OK,intent);
                for (int i = 0; i < mData.size(); i++) {
                    // System.out.println(mtxtfun.list.get(i).getKey() + ": " + mtxtfun.list.get(i).getValue());
                    //wDao.Insertword(mtxtfun.list.get(i).getKey());
                    wDao.Insertword(mData.get(i));
                }
                finish();
                break;
        }
        return item.getItemId()==R.id.action_cart?true:false;
    }
    //    private List<ListviewItem> getData() {
//        List<ListviewItem> data = new ArrayList<ListviewItem>();
//        for (int i = 0; i < mEwordList.size(); i++) {
//            data.add(new ListviewItem("" + mEwordList.get(i).getWordSpell()));
//        }
//        Log.d("swipe", "getData() OK");
//        return data;
//    }
    private List<String> getData() {
        List<String> data = new ArrayList<String>();
        for (int i = 0; i < mEwordList.size(); i++) {
            data.add("" + mEwordList.get(i).getWordSpell());
        }
        Log.d("swipe", "getData() OK");
        return data;
    }
    //init -- copy the database
    private void init() {
        File dir = new File(SqlHelper.DB_PATH);
        if (!dir.exists()) dir.mkdir();
        if (!(new File(SqlHelper.DB_NAME)).exists()) {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(SqlHelper.DB_NAME);
                byte[] buffer = new byte[8192];
                int count = 0;
                InputStream is = getResources().openRawResource(R.raw.wordwarrior);
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_cancel:
                cancel();
                break;
            case R.id.ll_delete:
                delete();
                break;
            case R.id.ll_inverse:
                inverse();
                break;
            case R.id.ll_select_all:
                selectAll();
                break;
        }
    }

    private void cancel() {
        setStateCheckedMap(false);//将CheckBox的所有选中状态变成未选中
        mLlEditBar.setVisibility(View.GONE);//隐藏下方布局
        adapter.setShowCheckBox(false);//让CheckBox那个方框隐藏
        adapter.notifyDataSetChanged();//更新ListView
    }

    private void delete() {
        if (mCheckedData.size() == 0) {
            Toast.makeText(wordlist.this, "您还没有选中任何数据！", Toast.LENGTH_SHORT).show();
            return;
        }
        final CustomDialog dialog = new CustomDialog(this);
        dialog.show();
        dialog.setHintText("是否删除？");
        dialog.setLeftButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setRightButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                beSureDelete();
                dialog.dismiss();
            }
        });
    }

    private void beSureDelete() {
        for(int i=0;i<mCheckedData.size();i++){
            wDao.Deleteword(mCheckedData.get(i));
        }
        mData.removeAll(mCheckedData);//删除选中数据
        setStateCheckedMap(false);//将CheckBox的所有选中状态变成未选中
        mCheckedData.clear();//清空选中数据
        adapter.notifyDataSetChanged();
        Toast.makeText(wordlist.this, "删除成功", Toast.LENGTH_SHORT).show();
    }
    /**
     * 反选就是stateCheckedMap的值为true时变为false,false时变成true
     * */
    private void inverse() {
        mCheckedData.clear();
        for (int i = 0; i < mData.size(); i++) {
            if (stateCheckedMap.get(i)) {
                stateCheckedMap.put(i, false);
            } else {
                stateCheckedMap.put(i, true);
                mCheckedData.add(mData.get(i));
            }
            lvData.setItemChecked(i, stateCheckedMap.get(i));//这个好行可以控制ListView复用的问题，不设置这个会出现点击一个会选中多个
        }
        adapter.notifyDataSetChanged();
    }

    private void selectAll() {
        mCheckedData.clear();//清空之前选中数据
        if (isSelectedAll) {
            setStateCheckedMap(true);//将CheckBox的所有选中状态变成选中
            isSelectedAll = false;
            mCheckedData.addAll(mData);//把所有的数据添加到选中列表中
        } else {
            setStateCheckedMap(false);//将CheckBox的所有选中状态变成未选中
            isSelectedAll = true;
        }
        adapter.notifyDataSetChanged();
    }

    private void setOnListViewItemClickListener() {
        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(adapter.isShowCheckBox()) {
                    Log.d("click","isshowbox");
                    updateCheckBoxStatus(view, position);
                }
                else{
                    Log.d("click","isnotshowbox");
                    Intent intent = new Intent(wordlist.this, showMeaning.class);
                    //intent.setClass();    //参数一为当前Package的context，t当前Activity的context就是this，其他Package可能用到createPackageContex()参数二为你要打开的Activity的类名
                    intent.putExtra("word",mData.get(position));
                    Log.d("mData.get(position)",String.valueOf(mData.get(position)));
                    startActivityForResult(intent,4);


                }
            }
        });
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * 如果返回false那么click仍然会被调用,,先调用Long click，然后调用click。
     * 如果返回true那么click就会被吃掉，click就不会再被调用了
     * 在这里click即setOnItemClickListener
     */
    private void setOnListViewItemLongClickListener() {
        Log.d("LongClick","hi");
        lvData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mLlEditBar.setVisibility(View.VISIBLE);//显示下方布局
                adapter.setShowCheckBox(true);//CheckBox的那个方框显示
                updateCheckBoxStatus(view, position);
                return true;
            }
        });
    }

    private void updateCheckBoxStatus(View view, int position) {
        MyAdapter.ViewHolder holder = (MyAdapter.ViewHolder) view.getTag();
        holder.checkBox.toggle();//反转CheckBox的选中状态
        lvData.setItemChecked(position, holder.checkBox.isChecked());//长按ListView时选中按的那一项
        stateCheckedMap.put(position, holder.checkBox.isChecked());//存放CheckBox的选中状态
        if (holder.checkBox.isChecked()) {
            mCheckedData.add(mData.get(position));//CheckBox选中时，把这一项的数据加到选中数据列表
        } else {
            mCheckedData.remove(mData.get(position));//CheckBox未选中时，把这一项的数据从选中数据列表移除
        }
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        lvData = (ListView) findViewById(R.id.lv);
        mLlEditBar = findViewById(R.id.ll_edit_bar);

        findViewById(R.id.ll_cancel).setOnClickListener(this);
        findViewById(R.id.ll_delete).setOnClickListener(this);
        findViewById(R.id.ll_inverse).setOnClickListener(this);
        findViewById(R.id.ll_select_all).setOnClickListener(this);
        lvData.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private void initData() {
        for (int i = 0; i < 1000; i++) {
            mData.add("第" + i + "项");
        }
        setStateCheckedMap(false);
    }
    /**
     * 设置所有CheckBox的选中状态
     * */
    private void setStateCheckedMap(boolean isSelectedAll) {
        for (int i = 0; i < mData.size(); i++) {
            stateCheckedMap.put(i, isSelectedAll);
            lvData.setItemChecked(i, isSelectedAll);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        //super.onBackPressed();
    }
}

