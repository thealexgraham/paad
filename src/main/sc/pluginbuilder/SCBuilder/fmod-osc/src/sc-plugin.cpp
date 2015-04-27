/*==============================================================================
Gain DSP Plugin Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2014.

This example shows how to create a simple gain DSP effect.
==============================================================================*/

#ifdef WIN32
    #define _CRT_SECURE_NO_WARNINGS
#endif

#include <math.h>
#include <stdio.h>
#include <assert.h>
#include <windows.h>
#include <iostream>
#include <string>

#include "../ip/UdpSocket.h"
#include "../osc/OscOutboundPacketStream.h"

#include "../inc/fmod.hpp"

#define ADDRESS "127.0.0.1"

///=============================
///%%%CONST_DEFINES%%%

#define TOP_ROUTE "/DiscreteExample6"
static const int PORT = 57125;

///=============================

#define OUTPUT_BUFFER_SIZE 1024

#ifdef WIN32
    #define _CRT_SECURE_NO_WARNINGS
#endif

/*
	First thing the plugin does when it loads is sends a message to supercollider with a random ID
	SC then creates the instruments and sets listeners with this random ID
	Now that version of the plugin can operate independently of the other ones

*/

extern "C" {
    F_DECLSPEC F_DLLEXPORT FMOD_DSP_DESCRIPTION* F_STDCALL FMODGetDSPDescription();
}

enum
{
	///=============================
	///%%%PARAM_ENUMS%%%
	
	FMOD_OTHER_PARAM_MASTER_MASTERGAIN,

	
	FMOD_OTHER_PARAM_PERCENTSWITCH9_CHANGEPERCENT,

	
	FMOD_OTHER_PARAM_PERCENTWALK6_WALKPERCENT,

	
	FMOD_OTHER_PARAM_SIMPLESCALE3_LENGTHMAX,

	
	FMOD_OTHER_PARAM_SIMPLESCALE3_LENGTHMIN,

	
	FMOD_OTHER_PARAM_SIMPLESCALE3_OCTAVEMIN,

	
	FMOD_OTHER_PARAM_SIMPLESCALE3_OCTAVEMAX,

	
	FMOD_OTHER_PARAM_FEEDBACKDELAY11_FEEDBACK,

	
	FMOD_OTHER_PARAM_RANDOMPASS15_PASSPERCENT,

	
	FMOD_OTHER_PARAM_PERCENTSWITCH9_SWITCHPERCENT,

	///=============================
    FMOD_OTHER_NUM_PARAMETERS
};

FMOD_RESULT F_CALLBACK FMOD_Other_dspcreate       (FMOD_DSP_STATE *dsp_state);
FMOD_RESULT F_CALLBACK FMOD_Other_dsprelease      (FMOD_DSP_STATE *dsp_state);
FMOD_RESULT F_CALLBACK FMOD_Other_dspreset        (FMOD_DSP_STATE *dsp_state);
FMOD_RESULT F_CALLBACK FMOD_Other_dspread         (FMOD_DSP_STATE *dsp_state, float *inbuffer, float *outbuffer, unsigned int length, int inchannels, int *outchannels);
FMOD_RESULT F_CALLBACK FMOD_Other_dspsetparamfloat(FMOD_DSP_STATE *dsp_state, int index, float value);
FMOD_RESULT F_CALLBACK FMOD_Other_dspsetparamint  (FMOD_DSP_STATE *dsp_state, int index, int value);
FMOD_RESULT F_CALLBACK FMOD_Other_dspsetparambool (FMOD_DSP_STATE *dsp_state, int index, bool value);
FMOD_RESULT F_CALLBACK FMOD_Other_dspsetparamdata (FMOD_DSP_STATE *dsp_state, int index, void *data, unsigned int length);
FMOD_RESULT F_CALLBACK FMOD_Other_dspgetparamfloat(FMOD_DSP_STATE *dsp_state, int index, float *value, char *valuestr);
FMOD_RESULT F_CALLBACK FMOD_Other_dspgetparamint  (FMOD_DSP_STATE *dsp_state, int index, int *value, char *valuestr);
FMOD_RESULT F_CALLBACK FMOD_Other_dspgetparambool (FMOD_DSP_STATE *dsp_state, int index, bool *value, char *valuestr);
FMOD_RESULT F_CALLBACK FMOD_Other_dspgetparamdata (FMOD_DSP_STATE *dsp_state, int index, void **value, unsigned int *length, char *valuestr);

/// ==========================
///%%%STATIC_PARAM_DESC%%%
	
static FMOD_DSP_PARAMETER_DESC p_Master_masterGain; // p_Master_masterGain

	
static FMOD_DSP_PARAMETER_DESC p_PercentSwitch9_changePercent; // p_PercentSwitch9_changePercent

	
static FMOD_DSP_PARAMETER_DESC p_PercentWalk6_walkPercent; // p_PercentWalk6_walkPercent

	
static FMOD_DSP_PARAMETER_DESC p_SimpleScale3_lengthMax; // p_SimpleScale3_lengthMax

	
static FMOD_DSP_PARAMETER_DESC p_SimpleScale3_lengthMin; // p_SimpleScale3_lengthMin

	
static FMOD_DSP_PARAMETER_DESC p_SimpleScale3_octaveMin; // p_SimpleScale3_octaveMin

	
static FMOD_DSP_PARAMETER_DESC p_SimpleScale3_octaveMax; // p_SimpleScale3_octaveMax

	
static FMOD_DSP_PARAMETER_DESC p_feedbackdelay11_feedback; // p_feedbackdelay11_feedback

	
static FMOD_DSP_PARAMETER_DESC p_RandomPass15_passPercent; // p_RandomPass15_passPercent

	
static FMOD_DSP_PARAMETER_DESC p_PercentSwitch9_switchPercent; // p_PercentSwitch9_switchPercent

/// ===========================

FMOD_DSP_PARAMETER_DESC *FMOD_Other_dspparam[FMOD_OTHER_NUM_PARAMETERS] =
{
	/// =====================================
	///%%%PARAM_DESC_POINTERS%%%
	
	&p_Master_masterGain, 

	
	&p_PercentSwitch9_changePercent, 

	
	&p_PercentWalk6_walkPercent, 

	
	&p_SimpleScale3_lengthMax, 

	
	&p_SimpleScale3_lengthMin, 

	
	&p_SimpleScale3_octaveMin, 

	
	&p_SimpleScale3_octaveMax, 

	
	&p_feedbackdelay11_feedback, 

	
	&p_RandomPass15_passPercent, 

	
	&p_PercentSwitch9_switchPercent, 

	/// =====================================
};

FMOD_DSP_DESCRIPTION FMOD_Other_Desc =
{
    FMOD_PLUGIN_SDK_VERSION,
	/// =====================================
	///%%%DESC_NAME%%%
	
    "PAAD DiscreteExample6",

	/// =====================================
    0x00010000,     // plug-in version
    1,              // number of input buffers to process
    1,              // number of output buffers to process
    FMOD_Other_dspcreate,
    FMOD_Other_dsprelease,
    FMOD_Other_dspreset,
    FMOD_Other_dspread,
    0,
    0,
    FMOD_OTHER_NUM_PARAMETERS,
    FMOD_Other_dspparam,
    FMOD_Other_dspsetparamfloat,
    0, // FMOD_Other_dspsetparamint,
    0, //FMOD_Other_dspsetparambool,
    0, // FMOD_Other_dspsetparamdata,
    FMOD_Other_dspgetparamfloat,
    0, // FMOD_Other_dspgetparamint,
    0, //FMOD_Other_dspgetparambool,
    0, // FMOD_Other_dspgetparamdata,
    0,
    0
};

extern "C"
{

F_DECLSPEC F_DLLEXPORT FMOD_DSP_DESCRIPTION* F_STDCALL FMODGetDSPDescription()
{
	/// ====================================
	///%%%PARAM_DESCRIPTIONS%%%
	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_Master_masterGain, "masterGain", "f", "Adjusts masterGain", 0, 1, 0.5);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_PercentSwitch9_changePercent, "changePercent", "f", "Adjusts changePercent", 0, 100, 29.34);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_PercentWalk6_walkPercent, "walkPercent", "f", "Adjusts walkPercent", 0, 100, 25);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_SimpleScale3_lengthMax, "lengthMax", "f", "Adjusts lengthMax", 0, 10, 8);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_SimpleScale3_lengthMin, "lengthMin", "f", "Adjusts lengthMin", 0, 10, 4);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_SimpleScale3_octaveMin, "octaveMin", "f", "Adjusts octaveMin", -3, 3, 0);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_SimpleScale3_octaveMax, "octaveMax", "f", "Adjusts octaveMax", -3, 3, 1);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_feedbackdelay11_feedback, "feedback", "f", "Adjusts feedback", 0, 1, 0.2);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_RandomPass15_passPercent, "passPercent", "f", "Adjusts passPercent", 0, 100, 86.66);

	
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_PercentSwitch9_switchPercent, "switchPercent", "f", "Adjusts switchPercent", 0, 100, 10);

	/// ====================================

    return &FMOD_Other_Desc;
}

}

class FMODOtherState
{
public:
    FMODOtherState();

    void process(float *inbuffer, float *outbuffer, unsigned int length, int channels);
    void reset();

	/// ================================
	///%%%PARAM_CLASS_DECS%%%
	
	void setMaster_masterGain(float); 
	float Master_masterGain() const { return m_Master_masterGain; }  

	
	void setPercentSwitch9_changePercent(float); 
	float PercentSwitch9_changePercent() const { return m_PercentSwitch9_changePercent; }  

	
	void setPercentWalk6_walkPercent(float); 
	float PercentWalk6_walkPercent() const { return m_PercentWalk6_walkPercent; }  

	
	void setSimpleScale3_lengthMax(float); 
	float SimpleScale3_lengthMax() const { return m_SimpleScale3_lengthMax; }  

	
	void setSimpleScale3_lengthMin(float); 
	float SimpleScale3_lengthMin() const { return m_SimpleScale3_lengthMin; }  

	
	void setSimpleScale3_octaveMin(float); 
	float SimpleScale3_octaveMin() const { return m_SimpleScale3_octaveMin; }  

	
	void setSimpleScale3_octaveMax(float); 
	float SimpleScale3_octaveMax() const { return m_SimpleScale3_octaveMax; }  

	
	void setFeedbackdelay11_feedback(float); 
	float feedbackdelay11_feedback() const { return m_feedbackdelay11_feedback; }  

	
	void setRandomPass15_passPercent(float); 
	float RandomPass15_passPercent() const { return m_RandomPass15_passPercent; }  

	
	void setPercentSwitch9_switchPercent(float); 
	float PercentSwitch9_switchPercent() const { return m_PercentSwitch9_switchPercent; }  

	/// ================================

	void sendParam(const char *, const char *, const char *, float);
	void sendMsg(const char *, float);
	void setOSCID(int);

	int osc_id() const { return m_osc_id; }
	PROCESS_INFORMATION pi;

private:

	/// =============================
	///%%%PARAM_PRIVATE_DECS%%%
	
	float m_Master_masterGain; 

	
	float m_PercentSwitch9_changePercent; 

	
	float m_PercentWalk6_walkPercent; 

	
	float m_SimpleScale3_lengthMax; 

	
	float m_SimpleScale3_lengthMin; 

	
	float m_SimpleScale3_octaveMin; 

	
	float m_SimpleScale3_octaveMax; 

	
	float m_feedbackdelay11_feedback; 

	
	float m_RandomPass15_passPercent; 

	
	float m_PercentSwitch9_switchPercent; 

	/// ===============================

	int m_osc_id;
	bool was_set;
};

FMODOtherState::FMODOtherState()
{
    reset();
}

void FMODOtherState::setOSCID(int theID) {

	if (!was_set) {
		m_osc_id = theID;
		sendMsg("/start", m_osc_id);
		was_set = true;
	}
}

void FMODOtherState::process(float *inbuffer, float *outbuffer, unsigned int length, int channels)
{
	sendMsg("/process", 1);
}

void FMODOtherState::reset()
{

}

/// =======================================
///%%%PARAM_CLASS_FUNCTIONS%%%

void FMODOtherState::setMaster_masterGain(float value) { 
	m_Master_masterGain = value;
}


void FMODOtherState::setPercentSwitch9_changePercent(float value) { 
	m_PercentSwitch9_changePercent = value;
}


void FMODOtherState::setPercentWalk6_walkPercent(float value) { 
	m_PercentWalk6_walkPercent = value;
}


void FMODOtherState::setSimpleScale3_lengthMax(float value) { 
	m_SimpleScale3_lengthMax = value;
}


void FMODOtherState::setSimpleScale3_lengthMin(float value) { 
	m_SimpleScale3_lengthMin = value;
}


void FMODOtherState::setSimpleScale3_octaveMin(float value) { 
	m_SimpleScale3_octaveMin = value;
}


void FMODOtherState::setSimpleScale3_octaveMax(float value) { 
	m_SimpleScale3_octaveMax = value;
}


void FMODOtherState::setFeedbackdelay11_feedback(float value) { 
	m_feedbackdelay11_feedback = value;
}


void FMODOtherState::setRandomPass15_passPercent(float value) { 
	m_RandomPass15_passPercent = value;
}


void FMODOtherState::setPercentSwitch9_switchPercent(float value) { 
	m_PercentSwitch9_switchPercent = value;
}

/// =======================================

void FMODOtherState::sendParam(const char *addr, const char *param_name, const char *instance_id, float value) {

	UdpTransmitSocket transmitSocket( IpEndpointName( ADDRESS, PORT ) );
    
    char buffer[OUTPUT_BUFFER_SIZE];
    osc::OutboundPacketStream p( buffer, OUTPUT_BUFFER_SIZE );
    
    p << osc::BeginBundleImmediate
        << osc::BeginMessage( addr )
			<< "instance"
			<< param_name
			<< instance_id
            << value << osc::EndMessage
        << osc::EndBundle;
    
    transmitSocket.Send( p.Data(), p.Size() );
}

void FMODOtherState::sendMsg(const char *addr, float value) {

	//std::string address = TOP_ROUTE;
	//address += addr;

	UdpTransmitSocket transmitSocket( IpEndpointName( ADDRESS, PORT ) );
    
    char buffer[OUTPUT_BUFFER_SIZE];
    osc::OutboundPacketStream p( buffer, OUTPUT_BUFFER_SIZE );
    
    p << osc::BeginBundleImmediate
        << osc::BeginMessage( addr )
            << value << osc::EndMessage
        << osc::EndBundle;
    
    transmitSocket.Send( p.Data(), p.Size() );
}

FMOD_RESULT F_CALLBACK FMOD_Other_dspcreate(FMOD_DSP_STATE *dsp_state)
{
    dsp_state->plugindata = (FMODOtherState *)FMOD_DSP_STATE_MEMALLOC(dsp_state, sizeof(FMODOtherState), FMOD_MEMORY_NORMAL, "FMODOtherState");

	FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;

	char * command = "supercollider/sclang.exe";

	STARTUPINFO si;

    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &state->pi, sizeof(state->pi) );

    // Start the child process. 
    if( !CreateProcess( command,   // No module name (use command line)
        /// =====================================
		///%%%PROC_ARGS%%%
	
    " -d supercollider -l fmod/DiscreteExample6/sclang_conf.yaml -u 57125",

		/// =====================================
        NULL,           // Process handle not inheritable
        NULL,           // Thread handle not inheritable
        FALSE,          // Set handle inheritance to FALSE
        0,              // No creation flags
        NULL,           // Use parent's environment block
        NULL,           // Use parent's starting directory 
        &si,            // Pointer to STARTUPINFO structure
        &state->pi )           // Pointer to PROCESS_INFORMATION structure
    )

    if (!dsp_state->plugindata)
    {
        return FMOD_ERR_MEMORY;
    }
    return FMOD_OK;
}

FMOD_RESULT F_CALLBACK FMOD_Other_dsprelease(FMOD_DSP_STATE *dsp_state)
{
    FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;
	TerminateProcess(state->pi.hProcess, 1);
	//state->sendMsg("/dying", state->osc_id());
    FMOD_DSP_STATE_MEMFREE(dsp_state, state, FMOD_MEMORY_NORMAL, "FMODOtherState");
    return FMOD_OK;
}

FMOD_RESULT F_CALLBACK FMOD_Other_dspread(FMOD_DSP_STATE *dsp_state, float *inbuffer, float *outbuffer, unsigned int length, int inchannels, int * /*outchannels*/)
{
    FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;
    state->process(inbuffer, outbuffer, length, inchannels); // input and output channels count match for this effect
    return FMOD_OK;
}

FMOD_RESULT F_CALLBACK FMOD_Other_dspreset(FMOD_DSP_STATE *dsp_state)
{
    FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;
    state->reset();
    return FMOD_OK;
}

FMOD_RESULT F_CALLBACK FMOD_Other_dspsetparamfloat(FMOD_DSP_STATE *dsp_state, int index, float value)
{
    FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;

    switch (index)
    {
	/// ==============================================
	///%%%SET_PARAM_FLOAT%%%
	
	case FMOD_OTHER_PARAM_MASTER_MASTERGAIN:
		state->setMaster_masterGain(value);
		state->sendParam("/module/paramc", "masterGain", "1", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_PERCENTSWITCH9_CHANGEPERCENT:
		state->setPercentSwitch9_changePercent(value);
		state->sendParam("/module/paramc", "changePercent", "8", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_PERCENTWALK6_WALKPERCENT:
		state->setPercentWalk6_walkPercent(value);
		state->sendParam("/module/paramc", "walkPercent", "6", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_SIMPLESCALE3_LENGTHMAX:
		state->setSimpleScale3_lengthMax(value);
		state->sendParam("/module/paramc", "lengthMax", "2", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_SIMPLESCALE3_LENGTHMIN:
		state->setSimpleScale3_lengthMin(value);
		state->sendParam("/module/paramc", "lengthMin", "2", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_SIMPLESCALE3_OCTAVEMIN:
		state->setSimpleScale3_octaveMin(value);
		state->sendParam("/module/paramc", "octaveMin", "2", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_SIMPLESCALE3_OCTAVEMAX:
		state->setSimpleScale3_octaveMax(value);
		state->sendParam("/module/paramc", "octaveMax", "2", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_FEEDBACKDELAY11_FEEDBACK:
		state->setFeedbackdelay11_feedback(value);
		state->sendParam("/module/paramc", "feedback", "10", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_RANDOMPASS15_PASSPERCENT:
		state->setRandomPass15_passPercent(value);
		state->sendParam("/module/paramc", "passPercent", "15", value);
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_PERCENTSWITCH9_SWITCHPERCENT:
		state->setPercentSwitch9_switchPercent(value);
		state->sendParam("/module/paramc", "switchPercent", "8", value);
		return FMOD_OK;

	/// =============================================
	}
    return FMOD_ERR_INVALID_PARAM;
}

FMOD_RESULT F_CALLBACK FMOD_Other_dspgetparamfloat(FMOD_DSP_STATE *dsp_state, int index, float *value, char *valuestr)
{
    FMODOtherState *state = (FMODOtherState *)dsp_state->plugindata;

    switch (index)
    {
	/// ===============================================
	///%%%GET_PARAM_FLOAT%%%
	
	case FMOD_OTHER_PARAM_MASTER_MASTERGAIN:
		*value = state->Master_masterGain();
		if (valuestr) sprintf(valuestr, "% fl", state->Master_masterGain());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_PERCENTSWITCH9_CHANGEPERCENT:
		*value = state->PercentSwitch9_changePercent();
		if (valuestr) sprintf(valuestr, "% fl", state->PercentSwitch9_changePercent());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_PERCENTWALK6_WALKPERCENT:
		*value = state->PercentWalk6_walkPercent();
		if (valuestr) sprintf(valuestr, "% fl", state->PercentWalk6_walkPercent());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_SIMPLESCALE3_LENGTHMAX:
		*value = state->SimpleScale3_lengthMax();
		if (valuestr) sprintf(valuestr, "% fl", state->SimpleScale3_lengthMax());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_SIMPLESCALE3_LENGTHMIN:
		*value = state->SimpleScale3_lengthMin();
		if (valuestr) sprintf(valuestr, "% fl", state->SimpleScale3_lengthMin());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_SIMPLESCALE3_OCTAVEMIN:
		*value = state->SimpleScale3_octaveMin();
		if (valuestr) sprintf(valuestr, "% fl", state->SimpleScale3_octaveMin());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_SIMPLESCALE3_OCTAVEMAX:
		*value = state->SimpleScale3_octaveMax();
		if (valuestr) sprintf(valuestr, "% fl", state->SimpleScale3_octaveMax());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_FEEDBACKDELAY11_FEEDBACK:
		*value = state->feedbackdelay11_feedback();
		if (valuestr) sprintf(valuestr, "% fl", state->feedbackdelay11_feedback());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_RANDOMPASS15_PASSPERCENT:
		*value = state->RandomPass15_passPercent();
		if (valuestr) sprintf(valuestr, "% fl", state->RandomPass15_passPercent());
		return FMOD_OK;

	
	case FMOD_OTHER_PARAM_PERCENTSWITCH9_SWITCHPERCENT:
		*value = state->PercentSwitch9_switchPercent();
		if (valuestr) sprintf(valuestr, "% fl", state->PercentSwitch9_switchPercent());
		return FMOD_OK;

	/// ==============================================
	}

    return FMOD_ERR_INVALID_PARAM;
}
