package com.yuanding.schoolpass.view.rongyun;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.R.drawable;
import com.yuanding.schoolpass.R.id;
import com.yuanding.schoolpass.R.layout;
import com.yuanding.schoolpass.utils.PubMehods;
/**
 * 
 * @author MyPC自定义转发消息提供者
 *
 */
@ProviderTag ( messageContent = WYZFNoticeContent.class , showPortrait = true , centerInHorizontal = false )
public class CustomizeMessageItemProvider extends IContainerItemProvider.MessageProvider<WYZFNoticeContent> {

    class ViewHolder {
        TextView title;
        ImageView imageView;
        TextView message;
        RelativeLayout layout;
        LinearLayout linearLayout;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
    	
        View view = LayoutInflater.from(context).inflate(R.layout.z_conect_itme, null);
        ViewHolder holder = new ViewHolder();
        holder.title = (TextView) view.findViewById(R.id.title);
        holder.imageView = (ImageView) view.findViewById(R.id.image);
        holder.message = (TextView) view.findViewById(R.id.content);
        holder.layout = (RelativeLayout) view.findViewById(R.id.re);
        holder.linearLayout = (LinearLayout) view.findViewById(R.id.liner);
        view.setTag(holder);
        return view;
    }


    @Override
    public Spannable getContentSummary(WYZFNoticeContent data) {
        return new SpannableString(data.getTitleStr());
    }

  

	@Override
	public void bindView(View v, int arg1, WYZFNoticeContent arg2,
			UIMessage arg3) {
        ViewHolder holder = (ViewHolder) v.getTag();

        if (arg3.getMessageDirection()== UIMessage.MessageDirection.SEND) {//消息方向，自己发送的
            holder.layout.setBackgroundResource(R.drawable.rc_ic_bubble_right);
            holder.linearLayout.setPadding(100, 0, 0, 0);
        } else {
            holder.layout.setBackgroundResource(R.drawable.rc_ic_bubble_left);
            holder.linearLayout.setPadding(0, 0, 100, 0);
        }
        holder.title.setText(arg2.getTitleStr());
        holder.message.setText(arg2.getDetailStr());
    	ImageLoader imageLoader;
        DisplayImageOptions options;
        imageLoader = A_0_App.getInstance().getimageLoader();
        options = A_0_App.getInstance().getOptions(R.drawable.side_lost_posi, R.drawable.side_lost_posi, R.drawable.side_lost_posi);
       
        if(arg2.getImgUrl() != null && arg2.getImgUrl().length()>0 && !arg2.getImgUrl().equals("")){
        	holder.imageView.setVisibility(View.VISIBLE);
        	PubMehods.loadServicePic(imageLoader,arg2.getImgUrl(), holder.imageView, options);
		}else{
			holder.imageView.setVisibility(View.GONE);
		}
		
      // AndroidEmoji.ensure((Spannable) holder.message.getText());//显示消息中get的 Emoji 表情。
		
	}

	@Override
	public void onItemClick(View arg0, int arg1, WYZFNoticeContent arg2,
			UIMessage arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onItemLongClick(View arg0, int arg1, WYZFNoticeContent arg2,
			UIMessage arg3) {
		// TODO Auto-generated method stub
		
	}

}
