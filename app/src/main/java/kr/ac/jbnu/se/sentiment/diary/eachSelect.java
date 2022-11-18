package kr.ac.jbnu.se.sentiment.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class eachSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_select);
    }

}



class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {

    ArrayList<Person> items = new ArrayList<Person>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.activity_each_select, viewGroup, false);

        return new ViewHolder(itemView, viewGroup, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewholder, int position) {
        Person item = items.get(viewholder.getAdapterPosition());
        viewholder.setItem(item);

        int pkNums = Integer.parseInt(viewholder.pkNum.getText().toString());
        int pos = position;

        viewholder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dates = viewholder.selectDates.getText().toString();
                String contents = viewholder.selectContent.getText().toString();
                int pkNums = Integer.parseInt(viewholder.pkNum.getText().toString());
                int imgResIds = Integer.parseInt(viewholder.imgResId.getText().toString());//이미지 리소스 번호

                String picbits = viewholder.picbit.getText().toString();

                Intent intent = new Intent(v.getContext(), write.class);

                intent.putExtra("updateDate", dates);
                intent.putExtra("updateContent", contents);
                intent.putExtra("updatePkNum", pkNums);
                intent.putExtra("updateImgResId", imgResIds);
                intent.putExtra("updatePictureUri", picbits);
                intent.putExtra("position", pos);

                v.getContext().startActivity(intent);
            }
        });


        viewholder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("new Delete : "+ pkNums, "newDelete***");

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("일기 삭제");
                builder.setMessage("정말로 삭제할까요?");
                //builder.setIcon(android.R.drawable.ic_dialog_alert);

                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        viewholder.createDB(v.getContext());
                        viewholder.createTable();
                        viewholder.delete(pkNums);

                        items.remove(pos);
                        notifyDataSetChanged();

                    }
                });

                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        Log.d("uri***", viewholder.uriBool);

        if(!viewholder.uriBool.equals("none"))
        {
            viewholder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Intent intent = new Intent(v.getContext(), pictureBig.class);
//
//                    intent.putExtra("uriBool", viewholder.uriBool);
//
//                    v.getContext().startActivity(intent);

                }
            });
        }





    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Person item)
    {
        items.add(item);
    }

    public void setItems(ArrayList<Person> items)
    {
        this.items = items;
    }
    public Person getItem(int position)
    {
        return items.get(position);
    }

    public void setItem(int postion, Person item)
    {
        items.set(postion, item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        Button updateBtn, deleteBtn, selectDates;
        ImageView selectYellow, selectGreen, selectBlue, selectPink, selectRed, selectGray, selectHappy, selectSoso, selectSad;
        TextView selectContent;
        TextView pkNum;
        TextView imgResId;

        TextView picbit;//이미지의 bit번호

        SQLiteDatabase db;

        ImageView imageView;
        String uriBool;

        ImageView clear, thunderstorm, rain, snow, atmosphere, clouds;


        public ViewHolder(View itemView, ViewGroup viewGroup, int viewType) {
            super(itemView);


            updateBtn = itemView.findViewById(R.id.updateBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

            selectDates = itemView.findViewById(R.id.selectDates);

            selectHappy = itemView.findViewById(R.id.selectHappy);
            selectSoso = itemView.findViewById(R.id.selectSoso);
            selectSad = itemView.findViewById(R.id.selectSad);

            selectContent = itemView.findViewById(R.id.selectContent);
            pkNum = itemView.findViewById(R.id.pkNum);

            imgResId = itemView.findViewById(R.id.imgResId);
            picbit = itemView.findViewById(R.id.picbit);

            imageView = itemView.findViewById(R.id.imageView);

            clear = itemView.findViewById(R.id.clear);
            thunderstorm = itemView.findViewById(R.id.thunderstorm);
            rain = itemView.findViewById(R.id.rain);
            snow = itemView.findViewById(R.id.snow);
            atmosphere = itemView.findViewById(R.id.atmosphere);
            clouds = itemView.findViewById(R.id.clouds);

        }


        public void setItem(Person item)
        {
            selectDates.setText(item.getDates());

            selectContent.setText(item.getContent());
            pkNum.setText(item.getNum()+"");

            imgResId.setText(item.getMood()+"");
            picbit.setText(item.getPictureUri());



            uriBool = item.getPictureUri();

            if(item.getPictureUri().equals("none"))
            {
                //imageView.setVisibility(View.GONE);
                imageView.setImageResource(0);

                Log.d(item.getPictureUri(), "pci****");
            }
            else
            {
                imageView.setVisibility(View.VISIBLE);


                Uri uri = Uri.parse(item.getPictureUri());
                Glide.with(itemView.getContext()) .load(uri).override(450, 450) .into(imageView);
                //imageView.setImageResource(0);
                //imageView.setVisibility(View.GONE);
                //Log.d(item.getPictureUri(), "pci****");

            }

            selectHappy.setVisibility(View.INVISIBLE);
            selectSoso.setVisibility(View.INVISIBLE);
            selectSad.setVisibility(View.INVISIBLE);

            if(item.getMood() == 0) {
                selectHappy.setVisibility(View.VISIBLE);
            }
            if(item.getMood() == 1) {
                selectSad.setVisibility(View.VISIBLE);
            }
            if(item.getMood() == 2) {
                selectSoso.setVisibility(View.VISIBLE);
            }


            clear.setVisibility(View.INVISIBLE);
            thunderstorm.setVisibility(View.INVISIBLE);
            rain.setVisibility(View.INVISIBLE);
            snow.setVisibility(View.INVISIBLE);
            atmosphere.setVisibility(View.INVISIBLE);
            clouds.setVisibility(View.INVISIBLE);

            if(item.getWeather() == 0) {
                clear.setVisibility(View.VISIBLE);
            } else if(item.getWeather() == 1) {
                thunderstorm.setVisibility(View.VISIBLE);
            } else if(item.getWeather() == 2) {
                rain.setVisibility(View.VISIBLE);
            } else if(item.getWeather() == 3) {
                snow.setVisibility(View.VISIBLE);
            } else if(item.getWeather() == 4) {
                atmosphere.setVisibility(View.VISIBLE);
            } else if(item.getWeather() == 5) {
                clouds.setVisibility(View.VISIBLE);
            }
        }

        void delete(int pkNums) {
            String sql = "delete from mooda where num = " +pkNums+ ";";
            db.execSQL(sql);
        }
        void createDB(Context context) {
            db = context.openOrCreateDatabase("mooda", context.MODE_PRIVATE, null);
                    //openOrCreateDatabase("mooda", MODE_PRIVATE, null);
           // Log.d("db 생성", "writeDelete***");
        }

        void createTable() {
            String sql = "create table if not exists mooda " +
                    "(num integer primary key autoincrement, " +
                    "dates date, mood integer, content text, pictureUri text, weather integer);";
            db.execSQL(sql);
        }
        void dropTable() {
            String sql = "drop table mooda;";
            db.execSQL(sql);
        }
    }

}










