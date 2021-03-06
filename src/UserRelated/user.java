package UserRelated;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import SQLQuery.Base.*;

import javax.xml.transform.Result;

public class user extends SQLBase {
    //用户名
    public String userName=null;
    //用户标号
    public String userId = null;
    public String host = null;
    //查询相关
    //查询书名
    public String queryBookName=null;
    //查询作者名
    public String queryAuthorName=null;
    //查询出版社名
    public String queryPressName=null;
    public user(){ }
    //set相关 因为要进行可缺省查询 所以直接加%匹配所有 在查询中使用like 进行查询 查询完毕后 在进行升序排序
    // 完全匹配字典序最小 因为下一次比较为null对任意
    public void setQueryBookName(String queryBookName){
        this.queryBookName= queryBookName;
    }
    public void setQueryAuthorName(String queryAuthorName){
        this.queryAuthorName = queryAuthorName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setQueryPressName(String queryPressName){
        this.queryPressName=queryPressName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public void setUserHost(String host){
        this.host = host;
    }

    public String getQueryAuthorName() {
        return queryAuthorName;
    }

    public String getQueryBookName() {
        return queryBookName;
    }

    public String getQueryPressName() {
        return queryPressName;
    }

    public String[][] queryRenderInformation(){
        String[][] result = null;
        try{
            PreparedStatement pstmt = con.prepareStatement("select userId,rendinformation.bookId,bookinformation.bookName,lendDate,returnDate from rendinformation,bookinformation where userId = ? and rendinformation.bookId = bookinformation.bookId");
            pstmt.setString(1,userId);
            System.out.println("userId: "+userId);
            ResultSet rs = pstmt.executeQuery();
            rs.last();
            int row = rs.getRow();
            ResultSetMetaData metaData = rs.getMetaData();
            int col = metaData.getColumnCount();
            result = new String[row+1][col];
            int nowRow=0;
            System.out.println("col"+":"+" "+col+" row: "+row);
            rs.beforeFirst();
            while(rs.next()){
                System.out.println("nowRow"+":"+" "+nowRow);
                for(int i=0;i<col;i++){
                    System.out.println(i);
                    if(i==3 || i==4) {
                        java.sql.Date date = rs.getDate(i+1);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        result[nowRow][i] = sdf.format(date);
                    }
                    else result[nowRow][i]=rs.getString(i+1);
                }
                nowRow++;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return result;
    }
    public String[][] queryBook(){
        try{

            PreparedStatement pstmt = con.prepareStatement("select bookId,bookName,author,classification,press,status "+
                    "from bookinformation where bookName like ? and author like ? and press like ?");
            System.out.println(getQueryBookName());
            System.out.println(getQueryAuthorName());
            System.out.println(getQueryPressName());
            pstmt.setString(1,"%"+getQueryBookName()+"%");
            pstmt.setString(2,"%"+getQueryAuthorName()+"%");
            pstmt.setString(3,"%"+getQueryPressName()+"%");
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData;
            metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            rs.last();
            int recordAmount = rs.getRow();
            table = new String[recordAmount][columnCount];
            int i=0;
            rs.beforeFirst();
            while(rs.next()){
                for(int j=1;j<=columnCount;j++){
                    table[i][j-1]=rs.getString(j);
                    //                       System.out.print(table[i][j-1]+"\t");
                }
                i++;
            }

            return table;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public String makeQuerySQLCommand(){
        String SQLCommand = "select * from bookinformation";
        String FromInformation = " ";
        String QueryInformation = " ";
        if(queryBookName==null && queryAuthorName==null && queryPressName==null){
            return SQLCommand;
        }
        else{
            QueryInformation +=" where ";
        }
        // 添加书名查询 若queryBookName不为null 则
        // 对 where 约束条件进行追加
        // 因为bookName 为 bookInformation表的信息
        // 所以直接追加信息 即可
        if(queryBookName!=null) {
            QueryInformation+=" bookName like "+queryBookName+" ";
        }
        // 添加作者名查询 若queryAuthorName不为null 则
        // 对 from 表 进行追加
        // 因为authorInformation.authorId 与 bookInformation 为依赖关系 联立authorInformation
        // 对 where 约束条件进行追加
        // 使用伪链表 若前一项不为空 先追加 and
        // 再追加约束条件
        if(queryAuthorName!=null) {
            FromInformation += " , authorInformation ";
            if(queryBookName!=null) {
                QueryInformation +=" and ";
            }
            QueryInformation += " authorinformation.authorName likes "+queryAuthorName +" and authorinformation.authorId = "+
                    "bookinformation.authorId ";
        }
        // 添加作者名查询 若queryPressName不为null 则
        // 对 from 表 进行追加
        // 因为pressInformation.authorId 与 bookInformation 为依赖关系 联立pressInformation
        // 对 where 约束条件进行追加
        // 使用伪链表 若前两项不为空 先追加 and
        // 再追加约束条件
        if(queryPressName!=null){
            FromInformation += ", pressinformation ";
            if(queryAuthorName!=null){
                QueryInformation +=" and ";
            }else if(queryBookName!=null){
                QueryInformation +=" and ";
            }
            QueryInformation+=" pressinformation.pressName like "+queryPressName+" and pressinformation.pressId = "+
                    "bookinformation.pressId ";
        }
        SQLCommand = SQLCommand+FromInformation + QueryInformation;
        if(QueryInformation !=null){
            SQLCommand +=" order by bookName";
        }
        return SQLCommand;
    }
    //资源锁操作 未实现
    //执行借书操作
    //准备工作取得用户编号
    //准备工作检查该书是否借走
    //操作一 更新bookinformation
    //操作二 更新rendinformation
    //bookinformation中的status 1 为 正常可借阅 2 为已借出 3 为已收回但无法操作
    public void getUserId(){
//        String SQLQueryCommand = "select userId,userName from userinformation where host = "+'\''+host+'\'';
//        System.out.println(SQLQueryCommand);
//        query(SQLQueryCommand);
//        userId = table[1][0];
//        userName = table[1][1];
        try{
            PreparedStatement pstmt = con.prepareStatement("select userId,userName from userinformation where host = ?");
            pstmt.setString(1,host);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                userId = rs.getString("userId");
                userName = rs.getString("username");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    public boolean checkRend(String bookId){
//        String SQLCommand = "select status from bookInformation where bookId =  \'"+bookId+" \'";

        try{
            PreparedStatement pstmt = con.prepareStatement("select status from bookinformation where bookId = ?");
//            Statement statement = con.createStatement();
//            System.out.println(SQLCommand);
//            query(SQLCommand);
//            String Table[][] = table;
//            if(Table[1][0]=="1") return true;
//            else return false;
            pstmt.setString(1,bookId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                if( rs.getString("status").equals("1")) return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean rendBook(String bookId){
        if(userId ==null ||userName ==null) getUserId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date rendDate = new java.util.Date ();
        java.sql.Date RendDate = new java.sql.Date(rendDate.getTime());
        Calendar returndate = new GregorianCalendar();
        returndate.setTime(RendDate);
        returndate.add(returndate.DATE,30);
        java.util.Date returnDate = (java.util.Date)returndate.getTime();
        java.sql.Date ReturnDate =new java.sql.Date(returnDate.getTime());
        System.out.println("Rend + "+userId);
        try{

            PreparedStatement pstmt = con.prepareStatement("insert into rendinformation values (?,?,?,?);");
            pstmt.setString(1,userId);
            pstmt.setString(2,bookId);
            pstmt.setDate(3,RendDate);
            pstmt.setDate(4,ReturnDate);
            pstmt.executeUpdate();
            pstmt = con.prepareStatement("update bookinformation set status = 2 where bookId = ?");
            pstmt.setString(1,bookId);
            pstmt.executeUpdate();

        }
        catch(SQLException e) {
            return false;
        }
        return true;
    }
    //执行还书操作
    //准备工作取得用户编号
    //操作一 更新bookinformation
    //操作二 更新rendinformation
    public boolean returnBook(String bookId){
        if(userId ==null ||userName ==null) getUserId();
//        String SQLUpdateCommand = "update bookinformation set status = 3 where bookId = "+"\'"+bookId+"\'";
//        String SQLDeleteCommand = "delete from rendinformation where bookId = "+"\'"+bookId+"\'";
        try{
//            System.out.println("SQLUpdateCommand is "+SQLUpdateCommand);
//            System.out.println("SQLInsertCommand is "+SQLDeleteCommand);
            PreparedStatement pstmt = con.prepareStatement("update bookinformation set status = 3 where bookId = ?");
            pstmt.setString(1,bookId);
            pstmt.executeUpdate();
            pstmt.close();
            GetDBConnection("booklibrarymanager","root","HanDong85");
            pstmt = con.prepareStatement("delete from rendinformation where bookId = ?");
            pstmt.setString(1,bookId);
            pstmt.executeUpdate();

            System.out.println("Successfully return.");
        }
        catch(SQLException e) {
            return false;
        }
        return true;
    }
    //获得自己所属表里信息
    public String makeGetMessageSQLCommand(){
        if(userId==null || userName==null) getUserId();
        String SQLCommand = "select * from "+userId+"Message";
        System.out.println(SQLCommand);
        return SQLCommand;
    }
    public void sendMessageSQLCommand(String sendMessage){
        Date curtime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strCurTime = sdf.format(curtime);
        String SQLCommand = "insert into rootMessage values (\'"+userId+"\',\'"+strCurTime+"\',\'"+sendMessage+"\')";
        try{
            System.out.println(SQLCommand);
            PreparedStatement pstmt = con.prepareStatement("insert into rootmessage values (?,?,?,?)");
            pstmt.setString(1,userId);
            pstmt.setString(2,"GM");
            pstmt.setString(3,strCurTime);
            pstmt.setString(4,sendMessage.trim());
            pstmt.executeUpdate();
//            Statement statement = con.createStatement();
//            statement.executeUpdate(SQLCommand);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
}
