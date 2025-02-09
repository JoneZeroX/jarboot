import { Col, Row, Menu, Empty, Result, Button } from 'antd';
import ServerConfig from "@/components/setting/ServerConfig";
import CommonNotice from "@/common/CommonNotice";
import ErrorUtil from '@/common/ErrorUtil';
import styles from './index.less';
import ServerMgrService from "@/services/ServerMgrService";
import Logger from "@/common/Logger";
import {memo, useEffect, useState} from "react";
import { useIntl } from 'umi';
import {LoadingOutlined, SyncOutlined} from '@ant-design/icons';

const ServerSetting = memo(() => {
    const intl = useIntl();
    const [data, setData] = useState([]);
    const [current, setCurrent] = useState('');
    const [loading, setLoading] = useState(true);

    const query = () => {
        ServerMgrService.getServerList((resp: any) => {
            setLoading(false);
            if (resp.resultCode < 0) {
                CommonNotice.error(resp.resultMsg);
                return;
            }
            if (!(resp?.result instanceof Array)) {
                resp.result = new Array<any>();
            }
            setData(resp.result);
            if (resp.result.length > 0) {
                setCurrent(resp.result[0].name);
            }
        }, (errorMsg: any) => Logger.warn(`${ErrorUtil.formatErrResp(errorMsg)}`));
    };

    useEffect(query, []);

    const onSelect = (event: any) => {
        setCurrent(event.key);
    };
    if (loading) {
        return <Result icon={<LoadingOutlined/>} title={intl.formatMessage({id: 'LOADING'})}/>;
    }
    return <>{(data instanceof Array && data.length > 0) ? <Row>
        <Col span={6} className={styles.pageContainer}>
            <Menu
                onClick={onSelect}
                selectedKeys={[current]}
                mode="inline"
            >
                <Menu.ItemGroup title={<span>
                    <span>{intl.formatMessage({id: 'SERVER_LIST_TITLE'})}</span>
                    <Button type={"link"} onClick={query} style={{marginLeft: "50px"}}
                            icon={<SyncOutlined style={{color: 'green'}}/>}>
                        {intl.formatMessage({id: 'REFRESH_BTN'})}
                    </Button>
                </span>}>
                    <Menu.Divider/>
                    {data.map((item: any) => {
                        return <Menu.Item key={item.name}>{item.name}</Menu.Item>
                    })}
                </Menu.ItemGroup>
            </Menu>
        </Col>
        <Col span={18} className={styles.pageContainer}>
            <div style={{margin: '0 30px 0 5px', width: '80%'}}>
                <ServerConfig server={current}/>
            </div>
        </Col>
    </Row> : <Result icon={<Empty/>}
                     title={intl.formatMessage({id: 'SERVER_EMPTY'})}
                     extra={<Button type="primary" onClick={query}>
                         {intl.formatMessage({id: 'REFRESH_BTN'})}
                     </Button>}
    />}</>
});
export default ServerSetting;
