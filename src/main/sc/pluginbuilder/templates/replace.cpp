### ITERATE_START ###
	%%%PARAM_ENUMS%%%
	FMOD_OTHER_PARAM_^^instance_name^^_^^param_name^^,
### ITERATE_END ###

### SINGLE_START ###
%%%TOP_ROUTE%%%
define TOP_ROUTE "/^synth_name^"
### SINGLE_END ###

### SINGLE_START ###
%%%CONST_DEFINES%%%
define TOP_ROUTE "/^synth_name^"
static const int PORT = ^lang_port^;
### SINGLE_END ###

### SINGLE_START ###
	%%%PROC_ARGS%%%
    " -d supercollider -l fmod/^synth_name^/sclang_conf.yaml -u ^lang_port^",
### SINGLE_END ###

### SINGLE_START ###
	%%%DESC_NAME%%%
    "PAAD ^synth_name^",
### SINGLE_END ###

### ITERATE_START ###
	%%%STATIC_PARAM_DESC%%%
static FMOD_DSP_PARAMETER_DESC p_^instance_name^_^param_name^; // p_^instance_name^_^param_name^
### ITERATE_END ###

### ITERATE_START ###
	%%%PARAM_DESC_POINTERS%%%
	&p_^instance_name^_^param_name^, 
### ITERATE_END ###

### ITERATE_START ###
	%%%PARAM_DESCRIPTIONS%%%
	FMOD_DSP_INIT_PARAMDESC_FLOAT(p_^instance_name^_^param_name^, "^param_name^", "f", "Adjusts ^param_name^", ^min^, ^max^, ^default^);
### ITERATE_END ###

### ITERATE_START ###
	%%%PARAM_CLASS_DECS%%%
	void set^^instance_name^_^param_name^(float); 
	float ^instance_name^_^param_name^() const { return m_^instance_name^_^param_name^; }  
### ITERATE_END ###

### ITERATE_START ###
	%%%PARAM_PRIVATE_DECS%%%
	float m_^instance_name^_^param_name^; 
### ITERATE_END ###

### ITERATE_START ###
%%%PARAM_CLASS_FUNCTIONS%%%
void FMODOtherState::set^^instance_name^_^param_name^(float value) { 
	m_^instance_name^_^param_name^ = value;
}
### ITERATE_END ###

### ITERATE_START ###
	%%%SET_PARAM_FLOAT%%%
	case FMOD_OTHER_PARAM_^^instance_name^^_^^param_name^^:
		state->set^^instance_name^_^param_name^(value);
		state->sendParam("/^instance_type^/paramc", "^param_name^", "^instance_id^", value);
		return FMOD_OK;
### ITERATE_END ###

### ITERATE_START ###
	%%%GET_PARAM_FLOAT%%%
	case FMOD_OTHER_PARAM_^^instance_name^^_^^param_name^^:
		*value = state->^instance_name^_^param_name^();
		if (valuestr) sprintf(valuestr, "% fl", state->^instance_name^_^param_name^());
		return FMOD_OK;
### ITERATE_END ###