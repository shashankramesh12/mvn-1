package com.tyss.optimize.nlp.util;

public enum IfFailed {
	//	FAIL_THIS_STEP_AND_STOP_THE_CURRENT_TEST,
//	FAIL_THIS_STEP_BUT_CONTINUE_THE_TEST,
//	MAKE_THIS_STEP_AS_WARNING,
//	SKIP_CURRENT_ITERATION_AND_CONTINUE_WITH_NEXT_ITERATION,
//	TERMINATE_ALL_ITERATIONS,
//	FAIL_THIS_STEP_AND_STOP_THE_CURRENT_MODULE,
//	FAIL_THIS_STEP_AND_STOP_THE_CURRENT_EXECUTION,
//	CONTINUE_SCRIPT_EXECUTION,
//	STOP_CURRENT_SCRIPT_EXECUTION,
//	STOP_CURRENT_MODULE_EXECUTION,
//	STOP_CURRENT_SUITE_EXECUTION;
	MARK_THIS_STEP_AS_FAILED_AND_CONTINUE_SCRIPT_EXECUTION,
	MARK_THIS_STEP_AS_WARNING_AND_CONTINUE_SCRIPT_EXECUTION,
	MARK_THIS_STEP_AS_WARNING_AND_CONTINUE_MODULE_EXECUTION,
	MARK_THIS_STEP_AS_FAILED_AND_STOP_SCRIPT_EXECUTION,
	MARK_THIS_STEP_AS_FAILED_AND_STOP_CURRENT_MODULE_EXECUTION,
	MARK_THIS_STEP_AS_FAILED_AND_STOP_COMPLETE_EXECUTION,
	MARK_THIS_STEP_AS_FAILED_AND_CONTINUE_SUITE_EXECUTION,
	MARK_THIS_STEP_AS_FAILED_AND_CONTINUE_MODULE_EXECUTION,
	MARK_THIS_STEP_AS_FAILED_AND_CONTINUE_STEP_GROUP_EXECUTION,
	MARK_THIS_STEP_AS_WARNING_AND_CONTINUE_STEP_GROUP_EXECUTION,
	MARK_THIS_STEP_AS_FAILED_AND_STOP_STEP_GROUP_EXECUTION,
	MARK_THIS_SCRIPT_AS_FAILED_AND_CONTINUE_DEPENDANT_SCRIPT_EXECUTION,
	MARK_THIS_SCRIPT_AS_FAILED_AND_STOP_DEPENDANT_SCRIPT_EXECUTION,
	MARK_THIS_SCRIPT_AS_FAILED_AND_STOP_CURRENT_MODULE_EXECUTION,
	MARK_THIS_SCRIPT_AS_FAILED_AND_STOP_COMPLETE_EXECUTION,
	MARK_THIS_STEP_AS_FAILED_AND_STOP_CURRENT_ITERATION,
	MARK_THIS_STEP_AS_FAILED_AND_STOP_ALL_ITERATIONS,
	MARK_THIS_STEP_AS_FAILED_AND_CONTINUE_CURRENT_ITERATION,
	MARK_THIS_SCRIPT_AS_WARNING_AND_CONTINUE_DEPENDANT_SCRIPT_EXECUTION
//	SKIP_CURRENT_ITERATION_AND_CONTINUE_WITH_NEXT_ITERATION,
//	TERMINATE_ALL_ITERATIONS
}