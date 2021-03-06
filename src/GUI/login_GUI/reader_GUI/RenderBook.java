package GUI.login_GUI.reader_GUI;

import UserRelated.user;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class RenderBook extends JPanel {
    JLabel bookName,authorName,pressName;
    JTextField bookText,authorText,pressText;
    JButton book,reset,check;
    JScrollPane jScrollPane;
    JTable jTable;
    String userId;
    RenderBook(String userId){
        this.userId = userId;
        init();

    }

    private class Information{
        ArrayList<String> dataArray;
        public Information(String...information){
            dataArray = new ArrayList<String>();
            for(String info:information){
                if(info.equals("1")) dataArray.add("可借阅");
                else if(info.equals("2")) dataArray.add("已借出");
                else if(info.equals("3")) dataArray.add("整理中");
                else dataArray.add(info);
            }
        }

        @Override
        public String toString() {
            String output=null;
            int cnt =0;
            for(String data:dataArray){
                output+=cnt+data+"  ";
                cnt++;
            }
            return output;
        }
    }
    class UserTableModel extends AbstractTableModel {
        String columnName[]={"书籍Id","书名","作者","分类","出版社","状态"};

        private static final long serialVersionUID = 1L;
        // 保存一个User的列表
        private List<Information> informationArray = new ArrayList<Information>();
        // 设置User列表, 同时通知JTabel数据对象更改, 重绘界面
        public void setList(ArrayList<Information> informationArray) {
            this.informationArray = informationArray;
            int cnt = 0;
            for(Information info:informationArray){
                System.out.println(info);
            }
            System.out.println(getColumnCount()+" "+getRowCount());
            this.fireTableDataChanged();// 同时通知JTabel数据对象更改, 重绘界面
        }
        public int getColumnCount() {
            return informationArray.get(0).dataArray.size();
        }
        public int getRowCount() {
            return informationArray.size();
        }
        public String getColumnName(int col){
            return columnName[col];
        }
        // 从list中拿出rowIndex行columnIndex列显示的值
        public Object getValueAt(int rowIndex, int columnIndex) {
            Information info =informationArray.get(rowIndex);
            return info.dataArray.get(columnIndex);
        }
    }


    void init(){
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        bookName = new JLabel("书  名 :   ");
        authorName = new JLabel("作 者 名 :   ");
        pressName = new JLabel("出 版 社 :   ");
        bookText = new JTextField(10);
        authorText = new JTextField(10);
        pressText = new JTextField(10);
        add(bookName);
        add(bookText);
        add(authorName);
        add(authorText);
        add(pressName);
        add(pressText);
        GridBagConstraints s = new GridBagConstraints();
        s.fill = GridBagConstraints.BOTH;
        s.gridx = 0;
        s.gridy = 0;
        s.gridwidth = 1;
        s.gridheight = 1;
        layout.setConstraints(bookName,s);
        s.gridx = 1;
        s.gridwidth = 2;
        layout.setConstraints(bookText,s);
        s.gridx = 3;
        s.gridwidth = 1;
        layout.setConstraints(authorName,s);
        s.gridx = 4;
        s.gridwidth = 2;
        layout.setConstraints(authorText,s);
        s.gridx = 6;
        s.gridwidth = 1;
        layout.setConstraints(pressName,s);
        s.gridx = 7;
        s.gridwidth=2;
        layout.setConstraints(pressText,s);
        JPanel blank1 = new JPanel();
        s.gridx = 0;
        s.gridy = 1;
        s.gridheight = 1;
        add(blank1);
        layout.setConstraints(blank1,s);
        check = new JButton("查询");

        final UserTableModel userTableModel = new UserTableModel();
        check.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bookMessage = bookText.getText();
                String authorMessage = authorText.getText();
                String pressMessage = pressText.getText();
                user reader = new user();
                reader.GetDBConnection("booklibrarymanager","root","HanDong85");
                reader.setQueryBookName(bookMessage);
                reader.setQueryAuthorName(authorMessage);
                reader.setQueryPressName(pressMessage);
                String[][] table = reader.queryBook();
//                String[] colName ={"书号","书名","作者","类别","出版社"};
                System.out.println("Now executing querying......");
               ArrayList<Information> list = new ArrayList<Information>();
                if(table!=null){
                    for(String[] array:table){
                        list.add(new Information(array));
                    }
                }else{
                    list.add(new Information("等待传参","等待传参","等待传参","等待传参","等待传参","等待传参"));
                }
                if(list.size()==0) System.out.println("ERROR");
                userTableModel.setList(list);
            }
        });


        s.gridx = 0;
        s.gridy = 2;
        s.gridwidth=3;
        add(check);
        layout.setConstraints(check,s);
        JPanel blank2 = new JPanel();
        s.gridx = 4;
        s.gridwidth = 2;
        add(blank2);
        layout.setConstraints(blank2,s);
        reset = new JButton("重  置");
        ArrayList<Information> list = new ArrayList<Information>();
        list.add(new Information("等待传参","等待传参","等待传参","等待传参","等待传参","等待传参"));
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookText.setText(null);
                authorText.setText(null);
                pressText.setText(null);
                userTableModel.setList(list);
            }
        });
        add(reset);
        s.gridx = 7;
        s.gridwidth=2;
        layout.setConstraints(reset,s);
        JPanel blank3 = new JPanel();
        add(blank3);
        s.gridy = 3;
        s.gridx = 0;
        s.gridheight=1;
        layout.setConstraints(blank3,s);
        jScrollPane = new JScrollPane();
//        dataJTable = new DataJTable();
//        jTable = new JTable();
        jTable = new JTable();


        userTableModel.setList(list);
        jTable.setModel(userTableModel);

//        jTable.setModel(dataJTable);
        JScrollPane jScrollPane = new JScrollPane(jTable);
        add(jScrollPane);
        s.gridy = 4;
        s.gridx = 0;
        s.gridwidth = 9;
        s.gridheight = 5;
        layout.setConstraints(jScrollPane,s);
        book = new JButton("借 阅");
        book.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = jTable.getSelectedRow();
                String bookId = (String)jTable.getValueAt(row,0);
                System.out.println("BookId : "+ bookId);
                user reader = new user();
                reader.userId = userId;
                reader.GetDBConnection("booklibrarymanager","root","HanDong85");
                if(reader.checkRend(bookId)==false){
                    JOptionPane.showMessageDialog(new JPanel(),"此书已借出");
                }else
                {
                    try{
                        reader.rendBook(bookId);
                        JOptionPane.showMessageDialog(new JPanel(),"借书成功");
                        userTableModel.setList(list);

                    }catch(Exception error){
                        error.printStackTrace();
                        JOptionPane.showMessageDialog(new JPanel(),"借书失败");
                    }
                }
            }
        });
        add(book);
        s.gridx = 3;
        s.gridy = 9;
        s.gridwidth=3;
        s.gridheight =1;
        layout.setConstraints(book,s);

    }
}
