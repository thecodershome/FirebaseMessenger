package g20.homework8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

public class FirebaseContactAdapter extends FirebaseRecyclerAdapter<UserObject, FirebaseContactAdapter.ProfileViewHolder> {

    private Firebase rb;
    private String myID;

    public FirebaseContactAdapter(Class<UserObject> modelClass, int modelLayout, Class<ProfileViewHolder> viewHolderClass, Firebase ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        myID = ref.getAuth().getUid();
        rb = ref.child(myID);
    }

    @Override
    protected void populateViewHolder(ProfileViewHolder profileViewHolder, UserObject mUser, int i) {

        if (!getRef(i).toString().equals(rb.toString())) {
            profileViewHolder.name.setText(mUser.getFullName());
            byte[] decodedString = Base64.decode(mUser.getPicture(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            if (bitmap != null)
                profileViewHolder.profile.setImageBitmap(bitmap);

            if (mUser.hasMsgStatus(myID))
                profileViewHolder.msgWait.setVisibility(View.VISIBLE);
            else
                profileViewHolder.msgWait.setVisibility(View.INVISIBLE);

            Bundle bun = new Bundle();
            bun.putString("ref", getRef(i).getKey());
            bun.putParcelable("user", mUser);

            profileViewHolder.itemView.setTag(bun);
            profileViewHolder.itemView.setOnClickListener((View.OnClickListener) profileViewHolder.mContext);

            profileViewHolder.phone.setTag(bun);
            profileViewHolder.phone.setOnClickListener((View.OnClickListener) profileViewHolder.mContext);
        } else {
            // Hide My Cell
            profileViewHolder.relativeLayout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        }

    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView profile, msgWait, phone;
        private Context mContext;
        private RelativeLayout relativeLayout;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.userCell);
            name = (TextView) itemView.findViewById(R.id.name_User);
            profile = (ImageView) itemView.findViewById(R.id.profile_img_User);
            msgWait = (ImageView) itemView.findViewById(R.id.message_img_User);
            phone = (ImageView) itemView.findViewById(R.id.phone_img_User);
            mContext = itemView.getContext();

        }
    }
}
