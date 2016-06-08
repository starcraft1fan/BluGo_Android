package com.example.user.blugo;

import android.graphics.Point;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 2016-06-08.
 */
public class BlutoothMsgParser {
    public enum MsgType {
        CHAT(0), /* chatting message */
        REQUEST_PLAY(1), /* PLAY REQUEST */
        REQUEST_PLAY_ACK(2), /* PLAY REQUEST ACK */
        PUTSTONE(3), /* PLAY REQUEST ACK */
        PASS(4),
        UNKNOWN(5);

        private final int value;

        private MsgType(int value) {
            this.value = value;
        }

        public static MsgType valueOf(int type)
        {
            /*
            Enumeration values must be sequential or else
            ArrayIndexoutofbound exeception may be thrown.
            */
            return (MsgType) MsgType.values()[type];
        }

        public int getValue()
        {
            return value;
        }
    }

    private static String[] msg_type_string = {
        "CHAT",
        "RQ_PLAY",
        "ACK_PLAY",
        "PUTSTONE",
        "PASS",
        "?????"
    };

    public class MsgParsed {
        MsgType type = MsgType.UNKNOWN;
        Object content = null;
    }

    private final Pattern command = Pattern.compile("(?m)\\[(.*?)\\](.*)");
    private final Pattern coord = Pattern.compile("(?m)([a-z])([a-z])");

    public static String make_message(MsgType type, Object opt)
    {
        String message;
        message = String.format("[%s]", msg_type_string[type.getValue()]);

        if (opt == null)
            return message;

        switch (type) {
            case PUTSTONE:
                Point p = (Point) opt;
                message += String.format("%c%c", (char) (p.x + 'a'), (char) (p.y + 'a'));
                break;
        }

        return message;
    }

    private Object parse_putstone(String opt)
    {
        Matcher m = coord.matcher(opt);
        String x, y;
        Point p;

        if (!m.find())
            return null;

        if (m.groupCount() < 2)
            return null;

        x = m.group(1);
        y = m.group(2);

        p = new Point();

        p.x = (int) (x.charAt(0) - 'a');
        p.y = (int) (y.charAt(0) - 'a');

        return p;
    }

    public MsgParsed parse(String message)
    {
        String cmd, opt;
        MsgType type;
        int i;

        /* Find pattern ';.*?' */
        Matcher m = command.matcher(message);
        MsgParsed parsed = new MsgParsed();

        if (!m.find())
            return parsed;

        if (m.groupCount() < 1)
            return parsed;

        cmd = m.group(1);
        opt = m.group(2);

        type = MsgType.UNKNOWN;

        for (i = 0 ; i < msg_type_string.length ; i++) {
            if (cmd.equals(msg_type_string[i])) {
                type = MsgType.values()[i];
                break;
            }
        }

        switch (type) {
            case CHAT:
                parsed.type = type;
                break;

            case REQUEST_PLAY_ACK:
                parsed.type = type;
                break;

            case REQUEST_PLAY:
                parsed.type = type;
                break;

            case PASS:
                parsed.type = type;
                break;

            case PUTSTONE:
                parsed.type = type;
                parsed.content = parse_putstone(opt);
                break;
        }

        return parsed;
    }
}