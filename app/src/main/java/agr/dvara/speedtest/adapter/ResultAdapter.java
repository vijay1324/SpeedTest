package agr.dvara.speedtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import agr.dvara.speedtest.R;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {


    private final LayoutInflater inflater;
    private final HashMap<String, HashMap<String, String>> values;
    private final ArrayList<Date> keys;

    public ResultAdapter(Context mContext, HashMap<String, HashMap<String, String>> values, ArrayList<Date> keys) {
        inflater = LayoutInflater.from(mContext);
        this.values = values;
        this.keys = keys;
    }

    @NonNull
    @NotNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ResultViewHolder(inflater.inflate(R.layout.adapter_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ResultViewHolder holder, int position) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss", Locale.ENGLISH);
        String timeStr = df.format(keys.get(position));
        holder.time.setText(String.format("Submit time: %s", timeStr.substring(0, timeStr.length() - 4)));
        holder.download.setText(String.format("Download speed: %s", Objects.requireNonNull(values.get(timeStr)).get("download")));
        holder.upload.setText(String.format("Upload speed: %s", Objects.requireNonNull(values.get(timeStr)).get("upload")));
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        private final TextView upload, download, time;

        public ResultViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            upload = itemView.findViewById(R.id.upload);
            download = itemView.findViewById(R.id.download);
            time = itemView.findViewById(R.id.timeTv);
        }
    }
}
