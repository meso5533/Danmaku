<?php
	require_once(DMK_LIB_PATH . '/api_base.php');
	require_once(DMK_LIB_PATH . '/danmaku_db.php');
	class ApiGetLatestID extends ApiBase{
		
		public function __construct(){
			parent::__construct(self::API_TYPE_GET);
		}

		private $mandatory = array('channel_id');

		protected function before(){
			$this->checkField($this->mandatory, NULL);
		}

		protected function process(){
			$db = DanmakuDb::getInstance();
			$latestID = $db->getLatestID($this->params['channel_id']);
			$this->outputSuccess($latestID);	
		}
	}

?>
