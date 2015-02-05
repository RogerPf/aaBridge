<?php

/**
 *     aa_pg_number
 *
 *     add line feeds and pg numbers to lin files (just for clarity of viewing)
 */
error_reporting( E_ALL);
date_default_timezone_set( 'Europe/London');

main();

exit( 0);

// ****************************************************************************
function main() {
	global $g;
	$g = new stdClass();
	
	$g->version = '0.02';

//	$g->input_override_filename = "c:\\a\\Negative Doubles____numbered" ; // omit the .lin
	

	/**
	 * Change lines into comments to give the standard operation
	 */
	
	global $pageCount;
	
	global $argv;
	
	if (isset( $g->input_override_filename)) {
		$fn_in = $g->input_override_filename . '.lin';
	}
	elseif (isset( $argv['1'])) {
		$fn_in = $argv['1'];
	}
	else {
		print "usage    scriptname.php <filename>" . PHP_EOL;
		exit( 2);
	}
	
	$fcheck = fopen( $fn_in, "rb");
	if ($fcheck === false) {
		print "cannot open file   $fn";
		exit( 2);
	}
	
	$fn_in = stream_get_meta_data( $fcheck)["uri"];
	fclose( $fcheck);
	
	if (isset( $g->output_override_filename)) {
		$fn_out = $g->output_override_filename . '__numbered.lin';
	}
	else {
		//$fn_out = pathinfo( $fn_in)['dirname'] . '/' . pathinfo( $fn_in)['filename'] . '__numbered.lin';
		$fn_out = $fn_in;
	}
	
	if (isset( $g->report_filename)) {
		$fr = fopen( $g->report_filename, "wb");
	}
	
	global $pageCount;
	
	$js = file_get_contents( $fn_in) . '  '; // read in source html file (any UTF8 text file)
	

	$fo = fopen( $fn_out, 'w'); // create the outputfile
	

	$state = 'cmd'; // we start in command hutting mode
	$out = '';
	
	$in_pg_data = false;
	$pg_count = 0;
	
	$l = strlen( $js);
	
	$a = '';
	$b = $js[0];
	$c = $js[1];
	
	for ($i = 2; $i < $l; $i++) {
		$a = $b;
		$b = $c;
		$c = $js[$i];
		
		if (($state == 'cmd') && ($c == '|') && ($a == 'p' || $a == 'P') && ($b == 'g' || $b == 'G')) {
			$in_pg_data = true;
			while ( true ) {
				$ol = strlen( $out) - 1;
				if ($out[$ol] == chr( 0x0a) || $out[$ol] == chr( 0x0d)) {
					$out = substr( $out, 0, $ol);
					continue;
				}
				break;
			}
			$out .= PHP_EOL . $a . $b . '| ***** ' . $pg_count++ . ' ***** ';
			$a = '';
			$b = '';
			$c = '';
			$state = 'data';
			continue;
		}
		else if (($state == 'cmd') && ($c == '|') && ($a == 'l' || $a == 'L') && ($b == 'b' || $b == 'B')) {
			$pg_count++;
			$state = 'data';
		}
		else if (($state = 'cmd') && ($c == '|')) {
			$state = 'data';
			if ($in_pg_data) {
				while ( true ) {
					if ($js[$i + 1] == chr( 0x0a) || $js[$i + 1] == chr( 0x0d)) {
						$i++;
						continue;
					}
					break;
				}
				$out .= $c . PHP_EOL . PHP_EOL . PHP_EOL;
				$a = '';
				$b = '';
				$c = '';
				$in_pg_data = false;
				continue; // as $c etc already added
			}
		}
		else if ($state == 'data' && ($c == '|')) {
			$state = 'cmd';
			$in_pg_data = false;
		}
		
		if ($in_pg_data == false) {
			$out .= $a;
		}
	}
	
	fwrite( $fo, $out);
	fclose( $fo);
	
	print "Done  $fn_in           $fn_out" . PHP_EOL;
	
	exit( 0);
}
