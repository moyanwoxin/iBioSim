package verification.platu.main;

public class Options {
	/*
	 * Levels given by an integer number to indicate the amount information to display
	 * while the tool is running
	 */
	private static int verbosity = 0;
	
	/*
	 * Path in the host where the DOT program is located 
	 */
	private static String dotPath = Main.workingDirectory;
	
	/*
	 * Timing analysis options:
	 */
	public static enum timingAnalysisDef {
		ON,			// no timing; zone - use regular zones; 
		ABSTRACTION	// merge zones to form convex hull for the same untimed state. 
	}
	private static String timingAnalysisType = "off";
	
	// Hao's Partial Order reduction options.
//	/*
//	 * Partial order reduction options
//	 */
//	public static enum PorDef { 
//		OFF,		// no POR 
//		STATIC, 	// POR  based on dependency relation by static analysis
//		BEHAVIORAL // POR based on dependency relation by behavioral analysis
//		};
//	private static String POR = "off";
	
	/*
	 * Partial order reduction options for ample set computation (not including ample compuation for cycle closing check).
	 */
	public static enum PorDef { 
		TRACEBACK, // normal POR
		NO_TRACEBACK // POR without trace-back	
		};
	private static String POR = "traceback";
	
	/*
	 * Cycle closing method for partial order reduction
	 */
	public static enum cycleClosingMethdDef{
		BEHAVIORAL, // Improved behavioral analysis on cycle closing
		STATE_SEARCH, // Improved behavioral analysis + state trace-back
		NO_CYCLECLOSING, // no cycle closing
		STRONG_CYCLECONDITION, // Strong cycle condition: for each cycle, at least one state has to fully expand.
	}
	private static String cycleClosingMethd = "behavioral";
	
	/*
	 * Ample computation during cycle closing check.
	 */
	public static enum cycleClosingAmpleMethdDef { 
		CC_TRACEBACK, // normal POR
		CC_NOTRACEBACK // POR without trace-back	
		};
	private static String cycleClosingAmpleMethd = "traceback";
	
	/*
	 * Output state graph (dot) flag
	 */
	private static boolean outputSgFlag = false;
	
	/*
	 * Debug mode : options to print intermediate results during POR
	 */
	private static boolean debug = false;
	
	/*
	 * Options for printing the final numbers from search_dfs or search_dfsPOR.
	 */
	private static boolean printLogToFile = false;
	
	/*
	 * Path for printing global state graph 
	 */
	private static String prjSgPath = null;
	
	/*
	 * Search algorithm options:
	 */
	public static enum searchTypeDef { 
		DFS, 			// DFS search on the entire state space
		BFS, 			// BFS on the entire state space.
		COMPOSITIONAL 	// using compositional search/reduction to build the reduce SG.
		};
		
	private static String searchType = "dfs";
	
	/*
	 * Options on how reachable states are stored.
	 */
	public static enum StateFormatDef {
		EXPLICIT,	// hash tables
		MDD,		// Mutli-Value DD
		BDD, 		// BDD
		AIG			// AIG
	}
	private static String stateFormat = "explicit";
	
	/*
	 * Use multi-threading when set to true.
	 */
	private static boolean parallelFlag = false;
	
	/*
	 * Option for compositional minimization type.
	 * off - no state space reduction
	 * abstraction - transition based abstraction
	 * reduction - state space reduction
	 */
	private static String compositionalMinimization = "off";
	
	private static boolean newParser = false;
	
	/*
	 * When true, use non-disabling semantics for transition firing.
	 */
	private static boolean stickySemantics = false;
	private static boolean timingAnalysisFlag = false;

	public static void setCompositionalMinimization(String minimizationType){
		if (minimizationType.equals("abstraction")){
    		compositionalMinimization = minimizationType;
    	}
    	else if (minimizationType.equals("reduction")){
    		compositionalMinimization = minimizationType;
    	}
    	else if (minimizationType.equals("off")){
    		
    	}
    	else{
    		System.out.println("warning: invalid COMPOSITIONAL_MINIMIZATION option - default is \"off\"");
    	}
	}
	
	public static String getCompositionalMinimization(){
		return compositionalMinimization;
	}
	
	public static boolean getTimingAnalysisFlag(){
		return timingAnalysisFlag;
	}
	
	public static void setStickySemantics(){
		stickySemantics = true;
	}
	
	public static boolean getStickySemantics(){
		return stickySemantics;
	}
	
	public static void setVerbosity(int v){
		verbosity = v;
	}
	
	public static int getVerbosity(){
		return verbosity;
	}
	
	public static void setDotPath(String path){
		dotPath = path;
	}
	
	public static String getDotPath(){
		return dotPath;
	}
	
	public static void setTimingAnalsysisType(String timing){
		if (timing.equals("zone")){
    		timingAnalysisFlag = true;
    		timingAnalysisType = timing;
    	}
    	else if (timing.equals("poset")){
    		timingAnalysisFlag = true;
    		timingAnalysisType = timing;
    	}
    	else if (timing.equals("off")){
    		// Alteration by Andrew N. Fisher
    		timingAnalysisFlag = false;
    		timingAnalysisType = "off";
    	}
    	else{
    		System.out.println("warning: invalid TIMING_ANALYSIS option - default is \"off\"");
    	}
	}
	
	public static String getTimingAnalysisType(){
		return timingAnalysisType;
	}
	
	public static void setPOR(String por){
		POR = por;
	}
	
	public static String getPOR(){
		return POR;
	}
	
	public static void setSearchType(String type){
		searchType = type;
	}
	
	public static String getSearchType(){
		return searchType;
	}
	
	public static void setStateFormat(String format){
		if (format.equals("explicit")){
 
    	}
    	else if (format.equals("bdd")){
    		stateFormat = format;
    	}
    	else if (format.equals("aig")){
    		stateFormat = format;
    	}
    	else if (format.equals("mdd")){
    		stateFormat = format;
    	}
    	else{
    		System.out.println("warning: invalid STATE_FORMAT option - default is \"explicit\"");
    	}
	}
	
	public static String getStateFormat(){
		return stateFormat;
	}
	
	public static void setParallelFlag(){
		parallelFlag = true;
	}
	
	public static boolean getParallelFlag(){
		return parallelFlag;
	}
	
	public static void setNewParser(){
		newParser = true;
	}
	
	public static boolean getNewParser(){
		return newParser;
	}

	public static void setCycleClosingMthd(String cycleclosing) {
		cycleClosingMethd = cycleclosing;
	}
	
	public static String getCycleClosingMthd() {
		return cycleClosingMethd;
	}
	
	public static void setOutputSgFlag(boolean outputSGflag) {
		outputSgFlag = outputSGflag;
	}
	
	public static boolean getOutputSgFlag() {
		return outputSgFlag;
	}

	public static void setPrjSgPath(String path) {
		prjSgPath = path;
	}
	public static String getPrjSgPath() {
		return prjSgPath;
	}

	public static void setCycleClosingAmpleMethd(String method) {
		cycleClosingAmpleMethd = method;		
	}
	
	public static String getCycleClosingAmpleMethd() {
		return cycleClosingAmpleMethd;
	}

	public static void setDebugMode(boolean debugMode) {
		debug = debugMode;	
	}
	
	public static boolean getDebugMode() {
		return debug;
	}

	public static void setPrintLogToFile(boolean printLog) {
		printLogToFile = printLog;
	}
	
	public static boolean getPrintLogToFile() {
		return printLogToFile;
	}
}
