package edu.kit.kastel.model;

import edu.kit.kastel.commands.SymbolSet;
import edu.kit.kastel.commands.VerbosityMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Renders a {@link GameBoard} according to the specified symbol set and verbosity mode.
 * Extracted from {@link GameBoard} to keep the board model small while preserving
 * the exact output format required by the specification.
 *
 * @author usylb
 */
final class BoardRenderer {

    /** Separator between row number and cell content. */
    private static final String ROW_NUMBER_SEPARATOR = " ";

    /** Indent for board row labels and separator lines. */
    private static final String BOARD_ROW_INDENT = "  ";

    /** Padding before column labels (A..G). */
    private static final String COLUMN_LABEL_LEFT_PADDING = "    ";

    /** Spacing between column labels. */
    private static final String COLUMN_LABEL_SPACING = "   ";

    /** Content for empty cell (3 spaces). */
    private static final String EMPTY_CELL_CONTENT = "   ";

    /** Custom symbol set: index for horizontal line (a-z order). */
    private static final int CUSTOM_SYMBOL_INDEX_HORIZONTAL = 8;

    /** Custom symbol set: index for vertical line. */
    private static final int CUSTOM_SYMBOL_INDEX_VERTICAL = 9;

    private static final int CUSTOM_SYMBOL_INDEX_TOP_LEFT_CORNER = 0;
    private static final int CUSTOM_SYMBOL_INDEX_TOP_RIGHT_CORNER = 1;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_LEFT_CORNER = 2;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_RIGHT_CORNER = 3;
    private static final int CUSTOM_SYMBOL_INDEX_TOP_MID = 4;
    private static final int CUSTOM_SYMBOL_INDEX_RIGHT_MID = 5;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_MID = 6;
    private static final int CUSTOM_SYMBOL_INDEX_LEFT_MID = 7;
    private static final int CUSTOM_SYMBOL_INDEX_CENTER = 10;

    private static final int CUSTOM_SYMBOL_INDEX_TOP_LEFT_CORNER_SELECTED = 11;
    private static final int CUSTOM_SYMBOL_INDEX_TOP_RIGHT_CORNER_SELECTED = 12;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_LEFT_CORNER_SELECTED = 13;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_RIGHT_CORNER_SELECTED = 14;
    private static final int CUSTOM_SYMBOL_INDEX_TOP_MID_SELECTED_LEFT = 15;
    private static final int CUSTOM_SYMBOL_INDEX_TOP_MID_SELECTED_RIGHT = 16;
    private static final int CUSTOM_SYMBOL_INDEX_RIGHT_MID_SELECTED_ABOVE = 17;
    private static final int CUSTOM_SYMBOL_INDEX_RIGHT_MID_SELECTED_BELOW = 18;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_MID_SELECTED_LEFT = 19;
    private static final int CUSTOM_SYMBOL_INDEX_BOTTOM_MID_SELECTED_RIGHT = 20;
    private static final int CUSTOM_SYMBOL_INDEX_LEFT_MID_SELECTED_ABOVE = 21;
    private static final int CUSTOM_SYMBOL_INDEX_LEFT_MID_SELECTED_BELOW = 22;
    private static final int CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_TOP_LEFT = 25;
    private static final int CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_TOP_RIGHT = 26;
    private static final int CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_BOTTOM_LEFT = 27;
    private static final int CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_BOTTOM_RIGHT = 28;

    /** Custom symbol set: index for horizontal line when selected. */
    private static final int CUSTOM_SYMBOL_INDEX_HORIZONTAL_SELECTED = 23;

    /** Custom symbol set: index for vertical line when selected. */
    private static final int CUSTOM_SYMBOL_INDEX_VERTICAL_SELECTED = 24;

    /** Standard symbols: corner, horizontal, vertical, then selected variants. */
    private static final char STD_CORNER = '+';
    private static final char STD_H = '-';
    private static final char STD_V = '|';
    private static final char STD_CORNER_SEL = '#';
    private static final char STD_H_SEL = '=';
    private static final char STD_V_SEL = 'N';

    private static final int MIN_ROW_INDEX = 0;
    private static final int MIN_COL_INDEX = 0;
    private static final int SENTINEL_ROW_FOR_BOTTOM_BORDER = -1;
    private static final int INDEX_OFFSET_ONE = 1;
    private static final int ROW_DISPLAY_OFFSET = 1;
    private static final char COLUMN_LABEL_FIRST_CHAR = 'A';
    private static final char CHAR_OWN_KING = 'X';
    private static final char CHAR_OWN_UNIT = 'x';
    private static final char CHAR_ENEMY_KING = 'Y';
    private static final char CHAR_ENEMY_UNIT = 'y';
    private static final char CHAR_CAN_MOVE = '*';
    private static final char CHAR_NO_MOVE = ' ';
    private static final char CHAR_BLOCK_SUFFIX = 'b';
    private static final char CHAR_NO_BLOCK = ' ';

    private BoardRenderer() {
    }

    /** Context for building a separator line; groups parameters to stay under the 7-parameter limit. */
    private record SeparatorContext(int rowBelow, int rowAbove, Field selectedField, boolean useStandard,
            char[] sym, char hNorm, char hSel, boolean isTopBorder, boolean isBottomBorder) { }

    /**
     * Renders the given board with the same format and semantics as
     * {@link GameBoard#render(Field, Team, Team)}.
     *
     * @param board         the board to render
     * @param selectedField the currently selected field, or null
     * @param teamShownAsX  the team whose units are shown as x/X
     * @param currentTeam   the team whose turn it is
     * @return list of output lines for the board
     */
    static List<String> render(GameBoard board, Field selectedField, Team teamShownAsX, Team currentTeam) {
        List<String> out = new ArrayList<>();
        VerbosityMode mode = board.getVerbosityMode();
        SymbolSet symbolSet = board.getSymbolSet();

        boolean compact = mode == VerbosityMode.COMPACT;
        boolean useStandard = !symbolSet.isCustom();
        char[] sym = symbolSet.raw();

        for (int r = GameBoard.SIZE - INDEX_OFFSET_ONE; r >= MIN_ROW_INDEX; r--) {
            if (!compact) {
                out.add(buildSeparatorLine(r, selectedField, useStandard, sym));
            }
            out.add(buildCellLine(board, r, selectedField, teamShownAsX, currentTeam, useStandard, sym));
        }
        if (!compact) {
            out.add(buildSeparatorLine(SENTINEL_ROW_FOR_BOTTOM_BORDER, selectedField, useStandard, sym));
        }
        out.add(buildColumnLabelLine());
        return out;
    }

    private static boolean isSelected(int row, int col, Field selectedField) {
        if (selectedField == null) {
            return false;
        }
        return selectedField.row() == row && selectedField.col() == col;
    }

    private static char getFirstJunctionChar(int rowBelow, int rowAbove, Field selectedField, boolean useStandard,
                                              char[] sym, boolean isTopBorder, boolean isBottomBorder) {
        if (useStandard) {
            boolean leftEdgeSel = selectedField != null
                    && (isSelected(rowBelow, MIN_COL_INDEX, selectedField)
                    || (rowAbove >= MIN_ROW_INDEX && isSelected(rowAbove, MIN_COL_INDEX, selectedField)));
            return leftEdgeSel ? STD_CORNER_SEL : STD_CORNER;
        }
        if (isTopBorder) {
            return isSelected(rowBelow, MIN_COL_INDEX, selectedField)
                    ? sym[CUSTOM_SYMBOL_INDEX_TOP_LEFT_CORNER_SELECTED]
                    : sym[CUSTOM_SYMBOL_INDEX_TOP_LEFT_CORNER];
        }
        if (isBottomBorder) {
            return isSelected(rowBelow, MIN_COL_INDEX, selectedField)
                    ? sym[CUSTOM_SYMBOL_INDEX_BOTTOM_LEFT_CORNER_SELECTED]
                    : sym[CUSTOM_SYMBOL_INDEX_BOTTOM_LEFT_CORNER];
        }
        return customLeftMidJunction(rowBelow, rowAbove, selectedField, sym);
    }

    private static void appendSegmentAndJunction(StringBuilder sb, int c, SeparatorContext ctx) {
        boolean segSel = isSegmentSelected(ctx.rowBelow(), ctx.rowAbove(), c, ctx.selectedField(),
                ctx.isTopBorder(), ctx.isBottomBorder());
        char hChar = segSel ? ctx.hSel() : ctx.hNorm();
        sb.append(hChar).append(hChar).append(hChar);
        boolean junctionSel = ctx.useStandard() && ctx.selectedField() != null
                && isStandardJunctionSelected(ctx.rowBelow(), ctx.rowAbove(), c,
                c == GameBoard.SIZE - INDEX_OFFSET_ONE, ctx.selectedField());
        char junction = ctx.useStandard()
                ? (junctionSel ? STD_CORNER_SEL : STD_CORNER)
                : customJunctionAfterCell(ctx.rowBelow(), ctx.rowAbove(), c, ctx.selectedField(),
                ctx.sym(), ctx.isTopBorder(), ctx.isBottomBorder());
        sb.append(junction);
    }

    private static String buildSeparatorLine(int row, Field selectedField, boolean useStandard, char[] sym) {
        int rowBelow = Math.max(row, MIN_ROW_INDEX);
        boolean inRange = row >= MIN_ROW_INDEX && row < GameBoard.SIZE - INDEX_OFFSET_ONE;
        int rowAbove = inRange ? row + INDEX_OFFSET_ONE : SENTINEL_ROW_FOR_BOTTOM_BORDER;
        boolean isTopBorder = row == GameBoard.SIZE - INDEX_OFFSET_ONE;
        boolean isBottomBorder = row == SENTINEL_ROW_FOR_BOTTOM_BORDER;

        StringBuilder sb = new StringBuilder();
        sb.append(BOARD_ROW_INDENT);
        char hNorm = useStandard ? STD_H : sym[CUSTOM_SYMBOL_INDEX_HORIZONTAL];
        char hSel = useStandard ? STD_H_SEL : sym[CUSTOM_SYMBOL_INDEX_HORIZONTAL_SELECTED];
        sb.append(getFirstJunctionChar(rowBelow, rowAbove, selectedField, useStandard, sym, isTopBorder, isBottomBorder));

        SeparatorContext ctx = new SeparatorContext(rowBelow, rowAbove, selectedField, useStandard, sym,
                hNorm, hSel, isTopBorder, isBottomBorder);
        for (int c = MIN_COL_INDEX; c < GameBoard.SIZE; c++) {
            appendSegmentAndJunction(sb, c, ctx);
        }
        return sb.toString();
    }

    private static boolean isStandardJunctionSelected(int rowBelow, int rowAbove, int junctionCol,
                                                      boolean isRightEdge, Field selectedField) {
        if (selectedField == null) {
            return false;
        }
        if (isRightEdge) {
            return isSelected(rowBelow, GameBoard.SIZE - INDEX_OFFSET_ONE, selectedField)
                    || (rowAbove >= MIN_ROW_INDEX && isSelected(rowAbove, GameBoard.SIZE - INDEX_OFFSET_ONE, selectedField));
        }
        return isSelected(rowBelow, junctionCol, selectedField)
                || isSelected(rowBelow, junctionCol + INDEX_OFFSET_ONE, selectedField)
                || (rowAbove >= MIN_ROW_INDEX && isSelected(rowAbove, junctionCol, selectedField))
                || (rowAbove >= MIN_ROW_INDEX && isSelected(rowAbove, junctionCol + INDEX_OFFSET_ONE, selectedField));
    }

    private static boolean isSegmentSelected(int rowBelow, int rowAbove, int col, Field selectedField,
                                             boolean isTopBorder, boolean isBottomBorder) {
        if (selectedField == null) {
            return false;
        }
        if (isTopBorder || isBottomBorder) {
            return isSelected(rowBelow, col, selectedField);
        }
        return isSelected(rowBelow, col, selectedField) || isSelected(rowAbove, col, selectedField);
    }

    private static char customLeftMidJunction(int rowBelow, int rowAbove, Field selectedField, char[] sym) {
        if (selectedField == null) {
            return sym[CUSTOM_SYMBOL_INDEX_LEFT_MID];
        }
        if (rowAbove >= MIN_ROW_INDEX && isSelected(rowAbove, MIN_COL_INDEX, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_LEFT_MID_SELECTED_ABOVE];
        }
        if (isSelected(rowBelow, MIN_COL_INDEX, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_LEFT_MID_SELECTED_BELOW];
        }
        return sym[CUSTOM_SYMBOL_INDEX_LEFT_MID];
    }

    private static char customRightMidJunction(int rowBelow, int rowAbove, Field selectedField, char[] sym) {
        if (selectedField == null) {
            return sym[CUSTOM_SYMBOL_INDEX_RIGHT_MID];
        }
        if (rowAbove >= MIN_ROW_INDEX && isSelected(rowAbove, GameBoard.SIZE - INDEX_OFFSET_ONE, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_RIGHT_MID_SELECTED_ABOVE];
        }
        if (isSelected(rowBelow, GameBoard.SIZE - INDEX_OFFSET_ONE, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_RIGHT_MID_SELECTED_BELOW];
        }
        return sym[CUSTOM_SYMBOL_INDEX_RIGHT_MID];
    }

    private static char customJunctionRightEdge(int rowBelow, int rowAbove, Field selectedField, char[] sym,
                                                boolean isTopBorder, boolean isBottomBorder) {
        if (isTopBorder) {
            return isSelected(rowBelow, GameBoard.SIZE - INDEX_OFFSET_ONE, selectedField)
                    ? sym[CUSTOM_SYMBOL_INDEX_TOP_RIGHT_CORNER_SELECTED]
                    : sym[CUSTOM_SYMBOL_INDEX_TOP_RIGHT_CORNER];
        }
        if (isBottomBorder) {
            return isSelected(rowBelow, GameBoard.SIZE - INDEX_OFFSET_ONE, selectedField)
                    ? sym[CUSTOM_SYMBOL_INDEX_BOTTOM_RIGHT_CORNER_SELECTED]
                    : sym[CUSTOM_SYMBOL_INDEX_BOTTOM_RIGHT_CORNER];
        }
        return customRightMidJunction(rowBelow, rowAbove, selectedField, sym);
    }

    private static char customJunctionAfterCell(int rowBelow, int rowAbove, int c, Field selectedField, char[] sym,
                                               boolean isTopBorder, boolean isBottomBorder) {
        if (c == GameBoard.SIZE - INDEX_OFFSET_ONE) {
            return customJunctionRightEdge(rowBelow, rowAbove, selectedField, sym, isTopBorder, isBottomBorder);
        }
        if (isTopBorder) {
            return customTopMidJunction(rowBelow, c, selectedField, sym);
        }
        if (isBottomBorder) {
            return customBottomMidJunction(rowBelow, c, selectedField, sym);
        }
        return customCenterJunction(rowBelow, rowAbove, c, selectedField, sym);
    }

    private static char midJunctionSymbol(int row, int c, Field selectedField, char[] sym,
                                          int idxNormal, int idxSelectedLeft, int idxSelectedRight) {
        if (selectedField == null) {
            return sym[idxNormal];
        }
        if (isSelected(row, c, selectedField)) {
            return sym[idxSelectedLeft];
        }
        if (isSelected(row, c + INDEX_OFFSET_ONE, selectedField)) {
            return sym[idxSelectedRight];
        }
        return sym[idxNormal];
    }

    private static char customTopMidJunction(int row, int c, Field selectedField, char[] sym) {
        return midJunctionSymbol(row, c, selectedField, sym,
                CUSTOM_SYMBOL_INDEX_TOP_MID,
                CUSTOM_SYMBOL_INDEX_TOP_MID_SELECTED_LEFT,
                CUSTOM_SYMBOL_INDEX_TOP_MID_SELECTED_RIGHT);
    }

    private static char customBottomMidJunction(int row, int c, Field selectedField, char[] sym) {
        return midJunctionSymbol(row, c, selectedField, sym,
                CUSTOM_SYMBOL_INDEX_BOTTOM_MID,
                CUSTOM_SYMBOL_INDEX_BOTTOM_MID_SELECTED_LEFT,
                CUSTOM_SYMBOL_INDEX_BOTTOM_MID_SELECTED_RIGHT);
    }

    private static char customCenterJunction(int rowBelow, int rowAbove, int c, Field selectedField, char[] sym) {
        if (selectedField == null) {
            return sym[CUSTOM_SYMBOL_INDEX_CENTER];
        }
        if (rowAbove >= MIN_ROW_INDEX && isSelected(rowAbove, c, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_TOP_LEFT];
        }
        if (rowAbove >= MIN_ROW_INDEX && isSelected(rowAbove, c + INDEX_OFFSET_ONE, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_TOP_RIGHT];
        }
        if (isSelected(rowBelow, c, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_BOTTOM_LEFT];
        }
        if (isSelected(rowBelow, c + INDEX_OFFSET_ONE, selectedField)) {
            return sym[CUSTOM_SYMBOL_INDEX_CENTER_SELECTED_BOTTOM_RIGHT];
        }
        return sym[CUSTOM_SYMBOL_INDEX_CENTER];
    }

    private static char getVerticalChar(boolean edgeSelected, boolean useStandard, char[] sym) {
        if (useStandard) {
            return edgeSelected ? STD_V_SEL : STD_V;
        }
        return edgeSelected && sym.length > CUSTOM_SYMBOL_INDEX_VERTICAL_SELECTED
                ? sym[CUSTOM_SYMBOL_INDEX_VERTICAL_SELECTED] : sym[CUSTOM_SYMBOL_INDEX_VERTICAL];
    }

    private static String buildCellLine(GameBoard board, int r, Field selectedField, Team teamShownAsX,
                                        Team currentTeam, boolean useStandard, char[] sym) {
        StringBuilder sb = new StringBuilder();
        sb.append(r + ROW_DISPLAY_OFFSET).append(ROW_NUMBER_SEPARATOR);
        for (int c = MIN_COL_INDEX; c < GameBoard.SIZE; c++) {
            boolean edgeSelected = isSelected(r, c, selectedField)
                    || (c > MIN_COL_INDEX && isSelected(r, c - INDEX_OFFSET_ONE, selectedField));
            sb.append(getVerticalChar(edgeSelected, useStandard, sym));
            sb.append(cellContent(board, r, c, teamShownAsX, currentTeam));
        }
        boolean rightEdgeSelected = isSelected(r, GameBoard.SIZE - INDEX_OFFSET_ONE, selectedField);
        sb.append(getVerticalChar(rightEdgeSelected, useStandard, sym));
        return sb.toString();
    }

    private static String buildColumnLabelLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(COLUMN_LABEL_LEFT_PADDING);
        for (int c = MIN_COL_INDEX; c < GameBoard.SIZE; c++) {
            if (c > MIN_COL_INDEX) {
                sb.append(COLUMN_LABEL_SPACING);
            }
            sb.append((char) (COLUMN_LABEL_FIRST_CHAR + c));
        }
        return sb.toString();
    }

    private static String cellContent(GameBoard board, int row, int col, Team teamShownAsX, Team currentTeam) {
        Field f = board.getField(row, col);
        Unit u = f.getUnit();
        if (u == null) {
            return EMPTY_CELL_CONTENT;
        }
        boolean ownForLetter = u.getTeam().equals(teamShownAsX);
        char c2 = ownForLetter ? (u.isKing() ? CHAR_OWN_KING : CHAR_OWN_UNIT) : (u.isKing() ? CHAR_ENEMY_KING : CHAR_ENEMY_UNIT);
        boolean canMove = u.getTeam().equals(currentTeam) && !u.hasMovedThisTurn();
        boolean block = u.isBlocked();
        char c1 = canMove ? CHAR_CAN_MOVE : CHAR_NO_MOVE;
        char c3 = block ? CHAR_BLOCK_SUFFIX : CHAR_NO_BLOCK;
        return "" + c1 + c2 + c3;
    }
}

