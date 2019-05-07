<?php
error_reporting(E_ALL);
/*
 * aa_filter - filter, anonymise, rotate randomise for Counting Practice
 */

global $g;
$g = new stdClass();
$g->version = 'v1';
$g->updated = '2019-05-02';
$g->show_progress = false; // 0 = don't stop otherwise a number to stop at

$g->anonymize = true; // if set true also rotates declarer / LHO LHO to south
$g->stop_after = 0; // 0 = don't stop otherwise a number to stop at

$g->f_bidding = strToUpper('rho'); // 'rho' or 'lho' or '' or anything or '' is no-restrictions

$g->forced_folder_out = '';

$g->f_minLevel = 2;
$g->f_maxLevel = 5;
$g->f_skip_suits = ''; // any of SHDC or N N = NT
$g->f_min_cards_played = 0; //
$g->f_extra_RHO_LHO_rotate = true;
$g->f_ramdomize = true;
$g->f_reverse = false;

$g->f_use_ct_filter = false;
$g->ct_filter = array(
    '2C' => 80,
    '2D' => 30,
    '2H' => 50,
    '2S' => 50,
    '2N' => 0,

    '3N' => 10,

    '4H' => 100,
    '4S' => 100,
    '4N' => 0,

    '5C' => 50,
    '5D' => 50,
    '5H' => 20,
    '5S' => 20,
    '5N' => 0
);

main();
exit(0);

/**
 * ****************************************************************************
 */
function main()
{
    global $g;
    global $argv;

    $fld_in = getcwd() . '/';
    $group_id = time();

    if (@$argv[1] === '-t') {
        $g->f_ramdomize = false;
        $g->f_reverse = true;
        $group_id = '0123456789';

        // $fld_in = getcwd() . '/kaplanStyl__2019-02/';
        $fld_in = getcwd() . '/kaplanStyl__2019-03/';
        // $fld_in = getcwd() . '/kaplanStyl__2019-04/';
        // $fld_in = getcwd() . '/test/';
        // $fld_in = getcwd() . '/tiny_test/';
    }

    if ($g->forced_folder_out !== '') {
        $g->folder_out = $g->forced_folder_out;
    }
    else {
        $g->folder_out = $fld_in;
        if ($g->f_bidding !== '') {
            $g->folder_out .= 'aa_' . $g->f_bidding . '/';
        }
        elseif ($g->anonymize) {
            $g->folder_out .= 'aa_ANON/';
        }
        else {
            $g->folder_out .= 'aa_FILTERED/';
        }
    }

    print PHP_EOL . basename($argv[0]) . '   ' . $g->version . '    ' . $g->updated . '     running on  PHP ' . phpversion() . PHP_EOL . PHP_EOL;

    print '  In--folder: ' . $fld_in . PHP_EOL;
    print '  Out-folder: ' . $g->folder_out . PHP_EOL . PHP_EOL;

    $g->dots_ct = 0;

    if ($g->show_progress == false) {
        pn_out('Please_wait');
    }

    $g->eol = PHP_EOL;
    if (file_exists("C:\\ProgramSmall\\aaBridge\\aaBridge__aaa_use_LF.txt")) {
        $g->eol = chr(10); // "LF" unix / Mac style
    }

    for ($i = (time() % 60) + 10; $i > 0; $i --) {
        rand(0, 99); // just to spin its wheels
    }

    $dir = new RecursiveDirectoryIterator($fld_in);
    $itr = new RecursiveIteratorIterator($dir, RecursiveDirectoryIterator::SKIP_DOTS);
    $files = new RegexIterator($itr, '/^.+\.lin$/i', RecursiveRegexIterator::GET_MATCH);

    $accepted = 0;
    $disc = 0;
    $all = array();

    foreach ($files as $fpath) {
        $lin = new Lin($fpath[0]);
        if ($lin->discard) {
            $disc ++;
            continue;
        }
        $all[] = $lin;
        pn_out('.');
        $accepted ++;
        // if ($g->f_stop_after > 0 && $accepted >= $g->f_stop_after) {
        // pn_out(" at $accepted ");
        // break;
        // }
    }

    pn_out("\n  Read-in: " . ($accepted + $disc) . "       Accepted: $accepted      Discarded: $disc \n");

    if ($g->anonymize) {
        if ($g->f_ramdomize) {
            shuffle($all);
        }
        elseif ($g->f_reverse) {
            $all = array_reverse($all);
        }
    }

    $count = 0;
    pn_out('Saving: ');

    @mkdir($g->folder_out);
    @array_map('unlink', @glob("$g->folder_out/*"));

    foreach ($all as $key => $lin) {

        $lin->save_to_folder_out($group_id, $key + 1);

        if (++ $count % 10) {
            pn_out('.');
        }

        if ($g->stop_after > 0 && $count >= $g->stop_after) {
            pn_out(" at $count ");
            break;
        }
    }
    pn_out(" Done \n");
}

// ===========================================================================
class Lin
{
    public $discard = true;
    public $first_line = '';
    public $contract = '';
    public $v_md_data = '';
    public $v_vun_letter = '';
    public $v_mc_full = '';
    public $v_card_play = '';
    public $data_debug_ONLY = array();

    // ===========================================================================
    function save_to_folder_out($group_id, $numb)
    {
        global $g;
        $eol = $g->eol;

        if ($g->anonymize == false) {

            $fn = $g->folder_out . $this->orig_filename . '.lin';
            $out = $this->asone;
        }
        else {

            $p2 = str_pad($numb, 3, '0', STR_PAD_LEFT);
            $p4 = str_pad($numb, 4, '0', STR_PAD_LEFT);

            $fn = $g->folder_out . $group_id . $p4 . ' ' . $p2 . '  -  _' . $this->contract . '      ' . $g->f_bidding . '   .lin';

            $cardPlay = $this->v_card_play;
            $cardPlay = preg_replace('~pg\|(.*?)\|~', "pg||$eol", $this->v_card_play);

            $out = '';
            $out .= $this->first_line . $eol;
            $out .= "qx|$numb|$eol";
            $out .= 'md|' . $this->v_md_data . "|$eol";
            $out .= "rh||ah|Board $numb|sv|" . $this->v_vun_letter . "|sk|s|$eol";
            $out .= $this->v_bidding . $eol;
            $out .= $cardPlay;
            if (strlen($this->v_mc_full) > 0) {
                $out .= $this->v_mc_full . 'pg||' . $eol;
            }
            // $out .= $eol . "qx|end,wide|$eol$eol";
        }

        file_put_contents($fn, $out);
    }

    // ===========================================================================
    function __construct($path)
    {
        global $g;

        $this->orig_filename = pathinfo($path, PATHINFO_FILENAME);

        $data = file($path, FILE_IGNORE_NEW_LINES);

        $this->asone = $asone = implode($data);

        $asone = preg_replace('~pn\|(.*?)\|~i', '', $asone);

        $matches = array();

        /*
         * extract the md|...| data ==============================================
         */
        preg_match('~md\|(.+?)\|~i', $asone, $matches);
        if (count($matches) < 2) {
            pn_out('a');
            return;
        }
        $this->v_md_data__ORIG = $matches[1];

        $orig_dealer_raw = $matches[1]{0}; // NOT adjusted to a zero base
        if ($orig_dealer_raw < 1) {
            pn_out('   skip -  dealer < 1 ??? :  ' . $path);
            return;
        }

        /*
         * Extract the bidding
         */
        preg_match('~mb\|(.*)\|mb\|~i', $asone, $matches);
        if (count($matches) < 2) {
            pn_out('b');
            return;
        }
        $this->v_bidding = $matches[0] . 'p|pg||'; // + adding the left behind bits

        /*
         * Unpack the bidding and try to make sense of the bidding and
         * find the contract
         * and inplement any filters
         * and find the declarer
         */
        $bids = explode('MB|', strToUpper($this->v_bidding));
        $ct = '';

        for ($i = (count($bids) - 1); $i > 0; $i --) { // 0 is an 'explode figment'
            $bid = $bids[$i];

            if ($bid{1} == 'N' || $bid{1} == 'S' || $bid{1} == 'H' || $bid{1} == 'D' || $bid{1} == 'C') {
                $ct = $this->contract = substr($bid, 0, 2);
                break;
            }
        }
        if ($ct == '' || $ct{0} < '1' || '7' < $ct{0}) {
            pn_out('p');
            return;
        }

        $declarer = '';
        foreach ($bids as $k => $bid) {
            if ($k == 0 || ($k % 2 != $i % 2)) {
                continue;
            }
            if ($bid{1} == $ct{1}) {
                $declarer = (8 + ($k - 1) + ($orig_dealer_raw - 1)) % 4; // $k-1 to "remove" extra zero entry
                break;
            }
        }

        if ($ct{0} < $g->f_minLevel) {
            pn_out('m');
            return;
        }

        if ($ct{0} > $g->f_maxLevel) {
            pn_out('M');
            return;
        }

        if (strlen($g->f_skip_suits) > 0) {
            $ln = strlen($g->f_skip_suits);
            for ($i = 0; $i < $ln; $i ++) {
                if ($ct{1} === $g->f_skip_suits{$i}) {
                    pn_out('s');
                    return;
                }
            }
        }

        if ($g->f_use_ct_filter && isset($g->ct_filter[$ct])) {
            $per = $g->ct_filter[$ct];
            if ($per <= rand(0, 99)) {
                pn_out('e');
                return;
            }
        }

        /*
         * Rotate the Declarer to South OR West if if RHO OR East if LHO
         */
        $targ = 0;
        if ($g->f_extra_RHO_LHO_rotate) {
            if ($g->f_bidding === 'RHO')
                $targ = 1;
            elseif ($g->f_bidding === 'LHO')
                $targ = 3;
        }

        $k = explode(',', $this->v_md_data__ORIG, 5);
        $k[0] = substr($k[0], 1); // remove existing dealer - was read in earlier
        $d_off = (4 + $declarer - $targ);
        $new_dealer_raw = $orig_dealer_raw;

        while ($d_off != 0) { // rotate hands anti clockwise
            $d_off --;
            if (-- $new_dealer_raw == 0) {
                $new_dealer_raw = 4;
            }
            $k0 = $k[0];
            $k[0] = $k[1];
            $k[1] = $k[2];
            $k[2] = $k[3];
            $k[3] = $k0;
        }
        $this->v_md_data = $new_dealer_raw . $k[0] . ',' . $k[1] . ',' . $k[2] . ',' . $k[3];

        /*
         * Scan the previously exploded bidding for hand OPPS filters e.g. rho
         */

        if (strlen($g->f_bidding) > 0) {

            $cld = array(
                0,
                0,
                0,
                0
            );

            $e = 3;
            $w = 1;

            if ($g->f_extra_RHO_LHO_rotate) {
                if ($g->f_bidding === 'RHO') {
                    $e = 0;
                    $w = 2;
                }
                elseif ($g->f_bidding === 'LHO') {
                    $e = 2;
                    $w = 0;
                }
            }

            $you = ($g->f_bidding === 'RHO') ? $e : $w;

            foreach ($bids as $k => $b) {
                if (strlen($b) == 0)
                    continue;

                $i = (($k - 1) + ($new_dealer_raw - 1)) % 4;

                if ($cld[$i] != 0)
                    continue;

                if (($g->f_bidding === 'RHO') || ($g->f_bidding === 'LHO')) {
                    if ($i == $you) {
                        if ($b{0} !== 'P') {
                            $cld[$i] = $k;
                        }
                        continue;
                    }
                    elseif (($b{0} == 'P') || ($b{0} == 'X') || ($b{0} == 'D') || ($b{1} == 'N')) {
                        continue;
                    }
                    else {
                        $cld[$i] = $k;
                        continue;
                    }
                }

                if (($b{0} == 'P') || ($b{0} == 'X') || ($b{0} == 'D') || ($b{1} == 'N')) {
                    continue;
                }
                $cld[$i] = $k;
            }

            $good = false;

            if ($g->f_bidding === 'RHO') {
                if (($cld[$e] == 0) && ($cld[$w] > 0) || ($cld[$e] > 0) && ($cld[$w] > 0) && ($cld[$e] > $cld[$w])) {
                    $good = true;
                }
            }
            elseif ($g->f_bidding === 'LHO') {
                if (($cld[$w] == 0) && ($cld[$e] > 0) || ($cld[$w] > 0) && ($cld[$e] > 0) && ($cld[$w] > $cld[$e])) {
                    $good = true;
                }
            }
            elseif (($cld[$e] > 0 || $cld[$w] > 0)) {
                $good = true;
            }

            if ($good === false) {
                pn_out('b');
                return;
            }
        }

        /*
         * Extract the vunerability
         */
        $vun = 'o';
        preg_match('~sv\|(.)\|~i', $asone, $matches);
        if (count($matches) > 1) {
            $vun = $matches[1]{0};

            if ($new_dealer_raw % 2 != $orig_dealer_raw % 2) {
                if ($vun === 'e' || $vun === 'w')
                    $vun = 'n';
                else
                    if ($vun === 'n' || $vun === 's')
                        $vun = 'e';
            }
        }
        $this->v_vun_letter = $vun;

        /*
         * Get the card play
         */
        $from = stripos($asone, 'pc|');
        if ($from < 0) {
            pn_out('c');
            return;
        }
        /* Find the last played card pos */
        $to = strripos($asone, 'pc|');
        if ($to > 0) {
            $to = strpos($asone, '|', $to + 3) + 1;
        }
        $this->v_card_play = substr($asone, $from, $to - $from) . 'pg||'; // as it (pg) will have been lost

        if ($g->f_min_cards_played > 0) {
            if (substr_count(strtolower($this->v_card_play), 'pc|') < $g->f_min_cards_played) {
                pn_out('c');
                return;
            }
        }

        preg_match('~mc\|(.+?)\|~i', $asone, $matches);
        if (count($matches) > 1) {
            $this->v_mc_full = $matches[0];
        }

        /*
         * Get / create the First saved date and any other info like LHO RHO
         */
        $ss_firstSaved = '%% Processed v2';
        $s = $data[0];
        $p = strpos($s, '|');
        if ($p !== false) {
            $s = substr($s, 0, $p);
        }
        if (startsWith($s, $ss_firstSaved)) {
            $this->first_line = $s;
        }
        else {
            $date = false;
            if (isset($data[2]) && startsWith($data[2], 'at|Played on ')) {
                preg_match('~([0-9][0-9][0-9][0-9]-[a-z][a-z][a-z]-[0-9][0-9])~i', $data[2], $matches);
                if (isset($matches[0]) && ($matches[0]) > 0) {
                    $date = DateTime::createFromFormat('Y-M-d', $matches[0]);
                }
            }
            if ($date === false) {
                $date = time(); // now
            }
            $this->first_line = $ss_firstSaved . '   ' . $date->format('Y-m-d') . '   ' . $g->f_bidding;
        }

        $this->discard = false;
    }
}

/**
 * ****************************************************************************
 */
function pn_out($s)
{
    global $g;

    $l = strlen($s);

    if ($l == 1 && $g->show_progress == false)
        return;

    if ($l > 10) {
        if ($g->dots_ct > 0) {
            print("\n");
        }
        $g->dots_ct = 0;
        print($s . "\n");
        return;
    }

    print($s);

    $g->dots_ct += $l;
    if ($g->dots_ct > 78) {
        $g->dots_ct = 0;
        print("\n");
    }
}

function startsWith($str, $needle)
{
    return strpos(strToLower($str), strToLower($needle)) === 0;
}

function contains($str, $needle)
{
    return strpos(strToLower($str), strToLower($needle)) !== false;
}

