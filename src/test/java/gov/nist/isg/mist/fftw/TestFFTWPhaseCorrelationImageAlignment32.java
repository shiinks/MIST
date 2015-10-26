// ================================================================
//
// Disclaimer: IMPORTANT: This software was developed at the National
// Institute of Standards and Technology by employees of the Federal
// Government in the course of their official duties. Pursuant to
// title 17 Section 105 of the United States Code this software is not
// subject to copyright protection and is in the public domain. This
// is an experimental system. NIST assumes no responsibility
// whatsoever for its use by other parties, and makes no guarantees,
// expressed or implied, about its quality, reliability, or any other
// characteristic. We would appreciate acknowledgment if the software
// is used. This software can be redistributed and/or modified freely
// provided that any derivative works bear some notice that they are
// derived from it, and any modified versions bear some notice that
// they have been modified.
//
// ================================================================

// ================================================================
//
// Author: tjb3
// Date: May 10, 2013 2:59:15 PM EST
//
// Time-stamp: <May 10, 2013 2:59:15 PM tjb3>
//
//
// ================================================================

package gov.nist.isg.mist.fftw;

import gov.nist.isg.mist.stitching.lib.common.CorrelationTriple;
import gov.nist.isg.mist.stitching.lib.imagetile.Stitching;
import gov.nist.isg.mist.stitching.lib.imagetile.fftw.FftwImageTile;
import gov.nist.isg.mist.stitching.lib.imagetile.memory.FftwTileWorkerMemory;
import gov.nist.isg.mist.stitching.lib.imagetile.memory.TileWorkerMemory;
import gov.nist.isg.mist.stitching.lib.imagetile.utilfns.UtilFnsStitching;
import gov.nist.isg.mist.stitching.lib.log.Debug;
import gov.nist.isg.mist.stitching.lib.log.Debug.DebugType;
import gov.nist.isg.mist.stitching.lib.log.Log;
import gov.nist.isg.mist.stitching.lib.log.Log.LogType;
import gov.nist.isg.mist.stitching.lib32.imagetile.Stitching32;
import gov.nist.isg.mist.stitching.lib32.imagetile.fftw.FFTW3Library32;
import gov.nist.isg.mist.stitching.lib32.imagetile.fftw.FftwImageTile32;
import gov.nist.isg.mist.stitching.lib32.imagetile.memory.FftwTileWorkerMemory32;
import gov.nist.isg.mist.timing.TimeUtil;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Test case for computing the phase correlation between two images using FFTW.
 * 
 * @author Tim Blattner
 * @version 1.0
 */
public class TestFFTWPhaseCorrelationImageAlignment32 {

  /**
   * Computes the phase correlation between two tiles using FFTW
   */
  public static void runTestPhaseCorrelationImageAlignment() throws FileNotFoundException {
    Log.setLogLevel(LogType.VERBOSE);
    Debug.setDebugLevel(DebugType.VERBOSE);
    UtilFnsStitching.disableUtilFnsNativeLibrary();
    Log.msg(LogType.MANDATORY, "Running Test Phase Correlation Image Alignment FFTW");

    // Read two images.
    File file1 = new File("C:\\majurski\\image-data\\1h_Wet_10Perc\\KB_2012_04_13_1hWet_10Perc_IR_00002.tif");
    File file2 = new File("C:\\majurski\\image-data\\1h_Wet_10Perc\\KB_2012_04_13_1hWet_10Perc_IR_00003.tif");

    FftwImageTile32 neighbor = new FftwImageTile32(file1, 0, 0, 2, 2, 0, 0);
    FftwImageTile32 origin = new FftwImageTile32(file2, 1, 0, 2, 2, 0, 0);

    Log.msg(LogType.INFO, neighbor.toString());
    Log.msg(LogType.INFO, origin.toString());

    if (FftwImageTile32.initLibrary("C:\\majurski\\NISTGithub\\MIST\\lib\\fftw", "", "libfftw3f")) {
      FftwImageTile tile = new FftwImageTile(file1);

      Log.msg(LogType.INFO, "Loading FFTW plan");
      TimeUtil.tick();
      FftwImageTile32.initPlans(tile.getWidth(), tile.getHeight(), FFTW3Library32.FFTW_MEASURE, true, "test.dat");
      Log.msg(LogType.INFO, "Loaded plan in " + TimeUtil.tock() + " ms");

      FftwImageTile32.savePlan("test.dat");

      TimeUtil.tick();
      TileWorkerMemory memory = new FftwTileWorkerMemory32(tile);
      CorrelationTriple result =
          Stitching32.phaseCorrelationImageAlignmentFftw(neighbor, origin, memory);

      Log.msg(
          LogType.MANDATORY,
          "Completed image alignment between " + neighbor.getFileName() + " and "
              + origin.getFileName() + " with " + result.toString() + " in " + TimeUtil.tock()
              + "ms");
    }
    Log.msg(LogType.MANDATORY, "Test Completed.");

  }

  /**
   * Executes the test case
   * 
   * @param args not used
   */
  public static void main(String[] args) {
      try {
          TestFFTWPhaseCorrelationImageAlignment32.runTestPhaseCorrelationImageAlignment();
      }catch (FileNotFoundException e)
      {
          Log.msg(LogType.MANDATORY, "Unable to find file: " + e.getMessage());
      }
  }

}