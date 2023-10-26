package com.board.make3;

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

            //데이터베이스 연결
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

        //boards 테이블에서 게시물 정보를 가져와서 출력하기
        try {
            String sql = "" +
                    "SELECT bno, btitle, bcontent, bwriter, bdate " +
                    "FROM boards " +
                    "ORDER BY bno DESC";//내림차순으로 정렬
            //conn 객체를 사용하여 sq에 정의된 SQL쿼리를 실행할 수 있는 PreparedStatement객체 생성
            PreparedStatement pstmt = conn.prepareStatement(sql);
            //pstmt를 사용하여 SQL 쿼리를 싱행하고 결과를 ResultSet의 객체 rs에 저장
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {//rs를 사용하여 테이터베이스의 결과 집합을 순회
                //새로운 정보를 저장할 인스턴스 생성
                //rs에서 결과를 출력하여 각 객체의 속성에 설정
                Board board = new Board();
                board.setBno(rs.getInt("bno"));
                board.setBtitle(rs.getString("btitle"));
                board.setBcontent(rs.getString("bcontent"));
                board.setBwriter(rs.getString("bwriter"));
                board.setBdate(rs.getDate("bdate"));
                //원하는 형식으로 결과 출력
                System.out.printf("%-6s%-12s%-16s%-40s\n", //printf라 ,로 연결
                        board.getBno(),
                        board.getBwriter(),
                        board.getBdate(),
                        board.getBtitle());

            }
            //ResultSet과 PreparedStatement 닫기
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            exit(); //SQLException이 발생한 경우 프로그램 종료
        }

        //메인 메뉴 출력
        mainMenu();
    }


    //메인 메뉴를 출력하고 사용자 입력을 받는 메소드 선언
    public void mainMenu() {
        boolean isValidMenu = false; //입력 확인
        while (!isValidMenu) { // 잘못된 메뉴를 선택하면 다시 메뉴 선택
            System.out.println();
            System.out.println("-----------------------------------------------------------------------");
            System.out.println("메인 메뉴: 1.Create | 2.Read | 3.Clear | 4.Exit");
            System.out.print("메뉴 선택: ");
            String menuNo = scanner.nextLine(); //메뉴 선택 입력
            System.out.println();
            //switch문을 사용하여 입력한 값에 따라 다른 동작
            switch (menuNo) { // 자바 11에서는 -> 사용 불가
                case "1":
                    create();//게시물 생성
                    isValidMenu = true;
                case "2":
                    read();//게시물 읽기
                    isValidMenu = true;
                case "3":
                    clear();//게시물 전체 삭제
                    isValidMenu = true;
                case "4":
                    exit();//프로그램 종료
                    isValidMenu = true;
                default:
                    System.out.println("잘못된 메뉴 선택");
                    System.out.println();
            }
        }
    }
    //게시물을 생성하는 메소드 선언
    public void create() {
        //입력 받기
        //Board 클래스의 새로운 인스턴스 생성하여 board 변수에 할당
        Board board = new Board();
        System.out.println("[새 게시물 입력]");
        System.out.print("제목: ");
        board.setBtitle(scanner.nextLine()); //제목 입력
        System.out.print("내용: ");
        board.setBcontent(scanner.nextLine()); //내용 입력
        System.out.print("글쓴이: ");
        board.setBwriter(scanner.nextLine()); //글쓴이 입력

        boolean validMenu = false; //입력 확인
        do {
            // 보조 메뉴 출력
            System.out.println("-----------------------------------------------------------------------");
            System.out.println("보조 메뉴: 1.Ok | 2.Cancel");
            System.out.print("메뉴 선택: ");
            String menuNo = scanner.nextLine(); // 보조 메뉴 입력

            if (menuNo.equals("1")) {
                // 1을 선택한 경우, 게시물 정보를 데이터 베이스에 저장
                try {
                    String sql = "" +
                            "INSERT INTO boards (btitle, bcontent, bwriter, bdate) " +
                            "VALUES (?, ?, ?, now())"; // SQL 쿼리에 게시물 정보를 데이터베이스에 삽입
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, board.getBtitle()); // 제목 저장
                    pstmt.setString(2, board.getBcontent()); // 내용 저장
                    pstmt.setString(3, board.getBwriter()); // 글쓴이 저장
                    pstmt.executeUpdate(); // SQL 쿼리를 실행하여 데이터베이스에 정보 저장
                    pstmt.close();
                } catch (Exception e) {
                    e.printStackTrace(); // 오류 발생 시 StackTrack 출력
                    exit(); // 오류 발생 시 프로그램 종료
                }
                validMenu = true; // 올바른 보조 메뉴 선택시 종료
            } else if (menuNo.equals("2")) {
                validMenu = true;
            }
             else {
                System.out.println("잘못된 보조 메뉴 선택"); // 잘못 선택시 출력
                System.out.println();
            }
        } while (!validMenu);

        // 게시물 목록 출력
        list();
    }
    //게시물을 읽는 메소드 선언
    public void read() {
        boolean validInput = false; //입력 확인

        do {
            // 입력 받기
            System.out.println("[게시물 읽기]");
            System.out.print("bno: ");
            int bno = Integer.parseInt(scanner.nextLine()); // 게시물 번호 입력

            // 잘못된 게시물 번호 입력시 다시 선택
            do {
                // 게시물 출력
                try {
                    String sql = "" +
                            "SELECT bno, btitle, bcontent, bwriter, bdate " +
                            "FROM boards " +
                            "WHERE bno=?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, bno); //게시물 번호 값 설정
                    ResultSet rs = pstmt.executeQuery(); // SQL 쿼리 실행 및 결과를 ResultSet에 저장
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

                        // 메뉴 선택시
                        boolean validMenu = false;
                        do {
                            // 보조 메뉴 출력
                            System.out.println("-------------------------------------------------------------------");
                            System.out.println("보조 메뉴: 1.Update | 2.Delete | 3.List");
                            System.out.print("메뉴 선택: ");
                            String menuNo = scanner.nextLine();

                            if (menuNo.equals("1")) {
                                update(board); // 게시물 업데이트
                                validMenu = true; // 선택이므로 반복 종료
                            } else if (menuNo.equals("2")) {
                                delete(board); // 게시물 삭제
                                validMenu = true;
                            } else if (menuNo.equals("3")) {//보조 메뉴 종료
                                validMenu = true;
                            } else {
                                System.out.println("잘못된 보조 메뉴 선택"); // 잘못 선택시 출력
                                System.out.println();
                            }
                            //잘못된 보조 메뉴 선택시 반복
                        } while (!validMenu);

                        validInput = true; // 올바른 메뉴 선택시 종료
                    } else {
                        //잘못된 게시물 번호 입력시 출력 및 다시 목록으로 돌아감
                        System.out.println("해당 번호의 게시물이 없습니다.");
                        System.out.println();
                        validInput = true; // 게시물 번호가 잘못된 경우도 반복 종료
                    }
                    rs.close();
                    pstmt.close();
                } catch (Exception e) {
                    e.printStackTrace(); // 오류 발생 시 StackTrace 출력
                    exit(); // 오류 발생 시 프로그램 종료
                }
            } while (!validInput);
        } while (!validInput);

        // 게시물 목록 출력
        list();
    }
    // 게시물 수정하는 메소드 선언
    public void update(Board board) {
        // 수정 내용 입력 받기
        System.out.println("[수정 내용 입력]");
        System.out.print("제목: ");
        board.setBtitle(scanner.nextLine());
        System.out.print("내용: ");
        board.setBcontent(scanner.nextLine());
        System.out.print("글쓴이: ");
        board.setBwriter(scanner.nextLine());

        boolean validMenu = false; //입력 확인

        do {
            // 보조 메뉴 출력
            System.out.println("-------------------------------------------------------------------");
            System.out.println("보조 메뉴: 1.Ok | 2.Cancel");
            System.out.print("메뉴 선택: ");
            String menuNo = scanner.nextLine(); // 보조 메뉴 입력

            if (menuNo.equals("1")) {
                // 1을 선택한 경우 게시물 정보 수정
                try {
                    String sql = "" +
                            "UPDATE boards SET btitle=?, bcontent=?, bwriter=? " +
                            "WHERE bno=?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, board.getBtitle());
                    pstmt.setString(2, board.getBcontent());
                    pstmt.setString(3, board.getBwriter());
                    pstmt.setInt(4, board.getBno());
                    pstmt.executeUpdate(); // SQL 쿼리 실행하여 게시물 정보 수정
                    pstmt.close();
                    validMenu = true; // 올바른 선택시 종료
                } catch (Exception e) {
                    e.printStackTrace(); // 오류 발생 시 StackTrace 출력
                    exit(); // 오류 발생 시 프로그램 종료
                }
            } else if (menuNo.equals("2")) {
                validMenu = true; // 반복 종료
            } else {
                System.out.println("잘못된 보조 메뉴 선택"); // 잘못 선택시 출력
                System.out.println();
            }
        } while (!validMenu);

        // 게시물 목록 출력
        list(); // 게시물 목록을 출력하는 메소드 호출
    }

    //게시물 삭제 메소드 선언
    public void delete(Board board) {
        boolean validInput = false; //입력 확인
        while (!validInput) {
            System.out.println("정말로 삭제하시겠습니까? (y/n)");
            String input = scanner.nextLine().toLowerCase();

            if (input.equals("y")) {
                // 삭제
                try {
                    String sql = "DELETE FROM boards WHERE bno=?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, board.getBno());
                    pstmt.executeUpdate();
                    pstmt.close();
                    validInput = true; // 올바른 입력
                } catch (Exception e) {
                    e.printStackTrace();
                    exit();
                }

                // 게시물 목록 출력
                list();
            } else if (input.equals("n")) {
                validInput = true;
            } else {
                System.out.println("잘못된 입력입니다. 다시 시도하세요.");
                System.out.println();
            }
        }
    }
    //게시물 전체 삭제 메소드 선언
    public void clear() {
        boolean validInput = false; //입력 확인

        while (!validInput) {
            System.out.println("[게시물 전체 삭제]");
            System.out.println("-------------------------------------------------------------------");
            System.out.println("보조메뉴: 1.Ok | 2.Cancel");
            System.out.print("메뉴선택: ");
            String menuNo = scanner.nextLine();

            if (menuNo.equals("1")) {
                // 게시물 전체 삭제
                try {
                    String sql = "TRUNCATE TABLE boards"; // 데이터 베이스에서 게시물 전체 삭제
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.executeUpdate();
                    pstmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    exit(); // 오류 발생 시 프로그램 종료
                }
                validInput = true; // 올바른 입력
            } else if (menuNo.equals("2")) {
                validInput = true; // 올바른 입력
            } else {
                System.out.println("잘못된 입력입니다. 다시 시도하세요."); // 잘못된 입력
                System.out.println();
            }
        }
    }
    public void exit() {
        System.out.println("게시판을 종료하시겠습니까? (y/n)");
        //.trim()은 입력된 문자열의 앞 뒤 공백을 제거
        //.toLowerCase()는 이볅된 문자열을 모두 소문자로 변환
        //response 변수에 저장
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("y")) {
            if (conn != null) {
                try {
                    conn.close(); //데이터 베이스 연결 정료
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("** 게시판 종료 **");
            System.exit(0); // 프로그램 종료
        } else {
            mainMenu(); //y 이외의 입력은 다시 메뉴로
        }
    }
    public static void main(String[] args) {
        BoardEx boardExam = new BoardEx();
        boardExam.list();
    }
}

