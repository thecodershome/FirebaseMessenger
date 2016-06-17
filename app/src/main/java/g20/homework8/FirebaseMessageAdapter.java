package g20.homework8;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

import java.util.Calendar;
import java.util.TimeZone;

public class FirebaseMessageAdapter extends FirebaseRecyclerAdapter<MessageObject, FirebaseMessageAdapter.MessageViewHolder> {

    public static final String TIMEFORMAT = "MM/dd/yyy hh:mm a";
    private UserObject myUserID, otherId;
    private String myID;

    public FirebaseMessageAdapter(Class<MessageObject> modelClass, int modelLayout, Class<MessageViewHolder> viewHolderClass, Firebase ref, UserObject me, UserObject other) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        myUserID = me;
        otherId = other;
        myID = ref.getAuth().getUid();
    }

    @Override
    protected void populateViewHolder(MessageViewHolder messageVH, MessageObject msg, int i) {

        if (msg.getSenderUid().equals(myID)) {
            messageVH.deleteImg.setVisibility(View.VISIBLE);
            messageVH.name.setText(myUserID.getFullName());
            messageVH.relativeLayout.setBackgroundColor(messageVH.mContext.getColor(R.color.lightGray));
        } else {
            messageVH.deleteImg.setVisibility(View.GONE);
            messageVH.name.setText(otherId.getFullName());
            messageVH.relativeLayout.setBackgroundColor(0);
        }

        messageVH.text.setText(msg.getText());

        Calendar cal = msg.getTimeStamp();
        cal.setTimeZone(TimeZone.getDefault());
        messageVH.time.setText(DateFormat.format(TIMEFORMAT, cal));

        messageVH.deleteImg.setTag(getRef(i));
        messageVH.deleteImg.setOnClickListener((View.OnClickListener) messageVH.mContext);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView name, text, time;
        private ImageView deleteImg;
        private Context mContext;
        private RelativeLayout relativeLayout;

        public MessageViewHolder(View itemView) {
            super(itemView);
            deleteImg = (ImageView) itemView.findViewById(R.id.delete_Message);
            name = (TextView) itemView.findViewById(R.id.name_Message);
            text = (TextView) itemView.findViewById(R.id.text_Message);
            time = (TextView) itemView.findViewById(R.id.time_Message);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.messageLayout_Message);
            mContext = itemView.getContext();
        }
    }
}
