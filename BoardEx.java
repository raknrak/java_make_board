import java.sql.*;
import java.util.Scanner;

public class BoardEx {
    private Scanner scanner = new Scanner(System.in);
    private Connection conn;

    //생성자
    public BoardEx() {
        try {
            //JDBC Driver 등록
            Class.forName("org.mariadb.jdbc.Driver");

            //데이터 베이스 연결
            conn = DriverManager.getConnection(
                    "jdbc:mariadb://127.0.0.1:3306/java_prj",
                    "root",
                    "12345");
        } catch (Exception e) {
            e.printStackTrace();
            exit();
        }
    }

    //목록을 출력하는 메소드 선언
    public void list() {
        //타이틀 및 컬럼명 출력
        System.out.println();
        System.out.println("[게시물 목록]");
        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("%-6s%-12s%-16s%-40s\n", "no", "writer", "date", "title");
        System.out.println("-----------------------------------------------------------------------");


        try {
            String sql = "" +
                    "SELECT bno, btitle, bcontent, bwriter, bdate " +
                    "FROM boards " +
                    "ORDER BY bno DESC";
            //conn 객체를 사용하여 sq에 정의된 SQL쿼리를 실행할 수 있는
            //PreparedStatement객체 생성
            PreparedStatement pstmt = conn.prepareStatement(sql);
            //pstmt를 사용하여 SQL 쿼리를 싱행하고 결과를 ResultSet의 객체 rs레코드에 저장
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {///rs를 사용하여 테이터베이스의 결과 집합을 순회
                //새로운 정보를 저장할 인스턴스 생성
                //rs에서 결과를 출력하여 각 객체의 속성에 설정
                Board board = new Board();
                board.setBno(rs.getInt("bno"));
                board.setBtitle(rs.getString("btitle"));
                board.setBcontent(rs.getString("bcontent"));
                board.setBwriter(rs.getString("bwriter"));
                board.setBdate(rs.getDate("bdate"));
                //원하는 형식으로 결과 출력
                System.out.printf("%-6s%-12s%-16s%-40s\n",//printf라 ,로 연결
                        board.getBno(),
                        board.getBwriter(),
                        board.getBdate(),
                        board.getBtitle());

            }
            //ResultSet과 PreparedStatement 닫기
            rs.close();
            pstmt.close();
        } catch (SQLException e) {//SQLException이 발생한 경우 프로그램 종료
            e.printStackTrace();
            exit();
        }

        //메인 메뉴 출력
        mainMenu();
    }
    //메인 메뉴를 출력하고 사용자 입력을 받는 메소드 선언
    public void mainMenu() {
        System.out.println();
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("메인메뉴: 1.Create | 2.Read | 3.Clear | 4.Exit");
        System.out.print("메뉴선택: ");
        String menuNo = scanner.nextLine();
        System.out.println();

        switch (menuNo) {// 자바 11에서는 -> 사용 불가
            case "1":
                create();//게시물 생성
            case "2":
                read();//게시물 읽기
            case "3":
                clear();//게시물 전체 삭제
            case "4":
                exit();//프로그램 종료
        }
    }
    //게시물을 생성하는 메소드 선언
    public void create() {
        //입력 받기
        //게시물을 생성하는 메소드 선언
        Board board = new Board();
        System.out.println("[새 게시물 입력]");
        System.out.print("제목: ");
        board.setBtitle(scanner.nextLine());//제목 입력
        System.out.print("내용: ");
        board.setBcontent(scanner.nextLine());//내용 입력
        System.out.print("글쓴이: ");
        board.setBwriter(scanner.nextLine());//글쓴이 입력

        //보조메뉴 출력
        System.out.println("-----------------------------------------------------------------------");
        System.out.println("보조메뉴: 1.Ok | 2.Cancel");
        System.out.print("메뉴선택: ");
        String menuNo = scanner.nextLine();
        if (menuNo.equals("1")) {
            //게시물 정보 저장
            try {
                String sql = "" +
                        "INSERT INTO boards (btitle, bcontent, bwriter, bdate) " +
                        "VALUES (?, ?, ?, now())";// SQL 쿼리에 게시물 정보를 데이터베이스에 삽입
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, board.getBtitle());// 제목 저장
                pstmt.setString(2, board.getBcontent());// 내용 저장
                pstmt.setString(3, board.getBwriter());// 글쓴이 저장
                pstmt.executeUpdate();// SQL 쿼리를 실행하여 데이터베이스에 정보 저장
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();// 오류 발생 시 StackTrack 출력
                exit();// 오류 발생 시 프로그램 종료
            }
        }

        //게시물 목록 출력
        list();
    }
    //게시물을 읽는 메소드 선언
    public void read() {
        //입력 받기
        System.out.println("[게시물 읽기]");
        System.out.print("bno: ");
        int bno = Integer.parseInt(scanner.nextLine());

        // 게시물 출력
        try {
            String sql = "" +
                    "SELECT bno, btitle, bcontent, bwriter, bdate " +
                    "FROM boards " +
                    "WHERE bno=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bno);//게시물 번호 값 설정
            // SQL 쿼리 실행 및 결과를 ResultSet에 저장
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Board board = new Board();
                board.setBno(rs.getInt("bno"));
                board.setBtitle(rs.getString("btitle"));
                board.setBcontent(rs.getString("bcontent"));
                board.setBwriter(rs.getString("bwriter"));
                board.setBdate(rs.getDate("bdate"));
                // 게시물 정보 출력
                System.out.println("#############");
                System.out.println("번호: " + board.getBno());
                System.out.println("제목: " + board.getBtitle());
                System.out.println("내용: " + board.getBcontent());
                System.out.println("쓴이: " + board.getBwriter());
                System.out.println("날짜: " + board.getBdate());
                //보조메뉴 출력
                System.out.println("-------------------------------------------------------------------");
                System.out.println("보조메뉴: 1.Update | 2.Delete | 3.List");
                System.out.print("메뉴선택: ");
                String menuNo = scanner.nextLine();
                System.out.println();

                if (menuNo.equals("1")) {
                    update(board);
                } else if (menuNo.equals("2")) {
                    delete(board);
                }
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            exit();
        }

        //게시물 목록 출력
        list();
    }
    // 게시물 수정하는 메소드 선언
    public void update(Board board) {
        //수정 내용 입력 받기
        System.out.println("[수정 내용 입력]");
        System.out.print("제목: ");
        board.setBtitle(scanner.nextLine());
        System.out.print("내용: ");
        board.setBcontent(scanner.nextLine());
        System.out.print("글쓴이: ");
        board.setBwriter(scanner.nextLine());

        //보조메뉴 출력
        System.out.println("-------------------------------------------------------------------");
        System.out.println("보조메뉴: 1.Ok | 2.Cancel");
        System.out.print("메뉴선택: ");
        String menuNo = scanner.nextLine();
        if (menuNo.equals("1")) {
            //게시물 정보 수정
            try {
                String sql = "" +
                        "UPDATE boards SET btitle=?, bcontent=?, bwriter=? " +
                        "WHERE bno=?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, board.getBtitle());
                pstmt.setString(2, board.getBcontent());
                pstmt.setString(3, board.getBwriter());
                pstmt.setInt(4, board.getBno());
                pstmt.executeUpdate();
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
                exit();
            }
        }

        //게시물 목록 출력
        list();
    }

    public void delete(Board board) {
        //게시물 정보 삭제
        try {
            String sql = "DELETE FROM boards WHERE bno=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, board.getBno());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            exit();
        }

        //게시물 목록 출력
        list();
    }
    public void clear() {
        System.out.println("[게시물 전체 삭제]");
        System.out.println("-------------------------------------------------------------------");
        System.out.println("보조메뉴: 1.Ok | 2.Cancel");
        System.out.print("메뉴선택: ");
        String menuNo = scanner.nextLine();
        if(menuNo.equals("1")) {
            //게시물 정보 전체 삭제
            try {
                String sql = "TRUNCATE TABLE boards";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.executeUpdate();
                pstmt.close();
            } catch (Exception e) {
                e.printStackTrace();
                exit();
            }
        }

        //게시물 목록 출력
        list();
    }

    public void exit() {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
        System.out.println("** 게시판 종료 **");
        System.exit(0);
    }

    public static void main(String[] args) {
        BoardEx boardExam = new BoardEx();
        boardExam.list();
    }
}

