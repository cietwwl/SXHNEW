package com.joyveb.tlol.task;

import java.util.ArrayList;
import java.util.Calendar;

import org.keplerproject.luajava.LuaException;

import com.joyveb.tlol.DefaultJavaFunc;
import com.joyveb.tlol.LuaService;
import com.joyveb.tlol.TLOLJavaFunction;
import com.joyveb.tlol.role.RoleBean;

/**
 * 任务操作注册函数
 */
public enum TaskJavaFunc implements TLOLJavaFunction {
	/**
	 * RoleBean.getTasks()
	 * 
	 * @param 参数1：RoleBean
	 */
	GetRoleTask(new DefaultJavaFunc("_GetRoleTask") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((RoleBean) this.getParam(2).getObject()).getTasks());
			return 1;
		}
	}),
	
	/**
	 * Task.addTaskState()
	 * 
	 * @param 参数1：TaskState
	 */
	AddTaskState(new DefaultJavaFunc("_AddTaskState") {
		@Override
		public int execute() throws LuaException {
			((Task) this.getParam(2).getObject()).addTaskState((TaskState) this.getParam(3).getObject());
			return 0;
		}
	}),

	/**
	 * SubtaskState.complete()
	 * 
	 * @param 参数1：SubtaskState
	 */
	CompleteSubtask(new DefaultJavaFunc("_CompleteSubtask") {
		@Override
		public int execute() throws LuaException {
			((SubtaskState) this.getParam(2).getObject()).complete();
			return 0;
		}
	}),

	/**
	 * TaskState.complete()
	 * 
	 * @param 参数1：TaskState
	 */
	CompleteTask(new DefaultJavaFunc("_CompleteTask") {
		@Override
		public int execute() throws LuaException {
			((TaskState) this.getParam(2).getObject()).complete();
			return 0;
		}
	}),
	

	/**
	 * Task.delTaskState()
	 * 
	 * @param 参数1：Task
	 * @param 参数2：int taskid
	 */
	DelTaskState(new DefaultJavaFunc("_DelTaskState") {
		@Override
		public int execute() throws LuaException {
			((Task) this.getParam(2).getObject()).delTaskState((int) this.getParam(3).getNumber());
			return 0;
		}
	}),

	/**
	 * TaskState.getSubstate()
	 * 
	 * @param 参数1：TaskState
	 */
	GetSubState(new DefaultJavaFunc("_GetSubState") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((TaskState) this.getParam(2).getObject()).getSubstate());
			return 1;
		}
	}),
	
	
	

	/**
	 * SubtaskState.getStates.get()
	 * 
	 * @param 参数1：SubtaskState
	 * @param 参数2：int index
	 */
	GetSubstateI(new DefaultJavaFunc("_GetSubstateI") {
		@Override
		public int execute() throws LuaException {
			SubtaskState subtask = (SubtaskState) this.getParam(2).getObject();
			ArrayList<Integer> substate = subtask.getStates();

			int i = (int) this.getParam(3).getNumber();

			if(substate.size() < i) {
				for(int index = substate.size(); index < i; index++)
					substate.add(0);
			}

			LuaService.push(substate.get(i - 1));
			return 1;
		}
	}),

	/**
	 * Task.getTaskState()
	 * 
	 * @param 参数1：Task
	 * @param 参数2：int taskid
	 */
	GetTaskState(new DefaultJavaFunc("_GetTaskState") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Task) this.getParam(2).getObject()).getTaskState((int) this.getParam(3).getNumber()));
			return 1;
		}
	}),

	/**
	 * Task.getTaskStates()
	 * 
	 * @param 参数1：Task
	 */
	GetTaskStates(new DefaultJavaFunc("_GetTaskStates") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((Task) this.getParam(2).getObject()).getTaskStates());
			return 1;
		}
	}),

	/**
	 * TaskState.getStep()
	 * 
	 * @param 参数1：TaskState
	 */
	GetTaskStateStep(new DefaultJavaFunc("_GetTaskStateStep") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((TaskState) this.getParam(2).getObject()).getStep());
			return 1;
		}
	}),

	/**
	 * Task.CURVERSION
	 */
	GetTaskVersion(new DefaultJavaFunc("_GetTaskVersion") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(Task.CURVERSION);
			return 1;
		}
	}),

	/**
	 * SubtaskState.isFinished()
	 * 
	 * @param 参数1：SubtaskState
	 */
	IsSubtastFinished(new DefaultJavaFunc("_IsSubtastFinished") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((SubtaskState) this.getParam(2).getObject()).isFinished());
			return 1;
		}
	}),

	/**
	 * TaskState.isFinished()
	 * 
	 * @param 参数1：TaskState
	 */
	IsTaskFinished(new DefaultJavaFunc("_IsTaskFinished") {
		@Override
		public int execute() throws LuaException {
			LuaService.push(((TaskState) this.getParam(2).getObject()).isFinished());
			return 1;
		}
	}),

	/**
	 * Subtask.getStates().set(i - 1, value)
	 * 
	 * @param 参数1：SubtaskState
	 * @param 参数2：int index
	 * @param 参数3：int value
	 */
	SetSubstateI(new DefaultJavaFunc("_SetSubstateI") {
		@Override
		public int execute() throws LuaException {
			SubtaskState subtask = (SubtaskState) this.getParam(2).getObject();
			ArrayList<Integer> substate = subtask.getStates();

			int i = (int) this.getParam(3).getNumber();
			int value = (int) this.getParam(4).getNumber();

			if(substate.size() < i) {
				for(int index = substate.size(); index < i; index++)
					substate.add(0);
			}

			substate.set(i - 1, value);
			return 0;
		}
	}),

	/**
	 * TaskState.getExtra().get(i - 1)
	 * 
	 * @param 参数1：TaskState
	 * @param 参数2：int index
	 */
	TaskStateExtraGetI(new DefaultJavaFunc("_TaskStateExtraGetI") {
		@Override
		public int execute() throws LuaException {
			TaskState taskstate = (TaskState) this.getParam(2).getObject();
			
			ArrayList<Integer> extra = taskstate.getExtra();
			int i = (int) this.getParam(3).getNumber();
			if(extra.size() < i) {
				for(int index = extra.size(); index < i; index++)
					extra.add(0);
			} 

			LuaService.push(extra.get(i - 1));
			
			return 1;
		}
	}),

	/**
	 * TaskState.getExtra().set(i - 1, value)
	 * 
	 * @param 参数1：TaskState
	 * @param 参数2：int index
	 * @param 参数3：int value
	 */
	TaskStateExtraSetI(new DefaultJavaFunc("_TaskStateExtraSetI") {
		@Override
		public int execute() throws LuaException {
			TaskState taskstate = (TaskState) this.getParam(2).getObject();
			ArrayList<Integer> extra = taskstate.getExtra();

			int i = (int) this.getParam(3).getNumber();
			int value = (int) this.getParam(4).getNumber();
			if(extra.size() < i) {
				for(int index = extra.size(); index < i; index++)
					extra.add(0);
			}

			extra.set(i - 1, value);
			
			return 0;
		}
	}),

	/**
	 * TaskState.stepDown()
	 * 
	 * @param 参数1：TaskState
	 */
	TaskStateStepDown(new DefaultJavaFunc("_TaskStateStepDown") {
		@Override
		public int execute() throws LuaException {
			((TaskState) this.getParam(2).getObject()).stepDown();
			return 0;
		}
	}),

	/**
	 * TaskState.stepUp()
	 * 
	 * @param 参数1：TaskState
	 */
	TaskStateStepUp(new DefaultJavaFunc("_TaskStateStepUp") {
		@Override
		public int execute() throws LuaException {
			((TaskState) this.getParam(2).getObject()).stepUp();
			return 0;
		}
	}),
	
	TaskOnltStepUp(new DefaultJavaFunc("_TaskOnltStepUp") {
		@Override
		public int execute() throws LuaException {
			int x = (int)this.getParam(3).getNumber();
			((TaskState) this.getParam(2).getObject()).onlyStepUp((byte) x);
			return 0;
		}
	}),
	
	
	/**
	 * 得到要选择的任务池中的任务序号
	 * 
	 * _GetTaskColmunNumber
	 */
	GetTaskColmunNumber(new DefaultJavaFunc("_GetTaskColmunNumber") {
		@Override
		public int execute() throws LuaException {
			int id = ((RoleBean) this.getParam(2).getObject()).getId();
			Calendar calendar = Calendar.getInstance();
			int date = calendar.get(Calendar.DATE);
			int month = calendar.get(Calendar.MONTH);
			int count = (int)this.getParam(3).getNumber();
			int result = count - ((month + date + id) % count);
			LuaService.push((int) result);
			return 1;
		}
	}),
	
	/**
	 * RoleBean.getTasks().getMonitored().add(int)
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	TaskAddMonitor(new DefaultJavaFunc("_TaskAddMonitor") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).getTasks().getMonitored().add((int) this.getParam(3).getNumber());
			return 0;
		}
	}),
	
	/**
	 * RoleBean.getTasks().getMonitored().remove(int)
	 * 
	 * @param 参数1：RoleBean
	 * @param 参数2：int
	 */
	TaskDelMonitor(new DefaultJavaFunc("_TaskDelMonitor") {
		@Override
		public int execute() throws LuaException {
			((RoleBean) this.getParam(2).getObject()).getTasks().getMonitored().remove((int) this.getParam(3).getNumber());
			return 0;
		}
	});
	
	/**
	 * 实现默认的可注册Java函数
	 */
	private final DefaultJavaFunc jf;

	/**
	 * @param jf 可注册Java函数
	 */
	private TaskJavaFunc(final DefaultJavaFunc jf) {
		this.jf = jf;
	}

	@Override
	public void register() {
		jf.register();
	}

}
