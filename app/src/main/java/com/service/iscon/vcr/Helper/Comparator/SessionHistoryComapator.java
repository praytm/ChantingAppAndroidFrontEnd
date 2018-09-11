package com.service.iscon.vcr.helper.Comparator;

import com.service.iscon.vcr.model.SessionModel;
import java.util.Comparator;
/**
 * Created by priyanka on 25-03-2017.
 */

public class SessionHistoryComapator implements Comparator<SessionModel>  {
    @Override
    public int compare(SessionModel lhs, SessionModel rhs) {
        /*if(lhs.getOrder_Id() == -1){
            return 1;
        }else{*/
        return lhs.getDate().compareTo(rhs.getDate());
        //}
    }
}
