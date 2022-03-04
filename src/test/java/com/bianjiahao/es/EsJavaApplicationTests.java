package com.bianjiahao.es;

import com.bianjiahao.es.dao.TeacherMapper;
import com.bianjiahao.es.entity.Teacher;
import com.google.gson.Gson;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class EsJavaApplicationTests {


    @Autowired(required = false)
    private TeacherMapper teacherMapper;

    /**
     * 创建索引
     */
    @Test
    public void createIndex() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost",9200)
                )
        );
        CreateIndexRequest request = new CreateIndexRequest("teacher");
        request.settings(
                Settings.builder()
                .put("index.number_of_shards",3)
                .put("index.number_of_replicas",2)
        );
        try {
            CreateIndexResponse respons = client.indices().create(request, RequestOptions.DEFAULT);
            if (respons.isAcknowledged()){
                System.out.println("创建索引成功！");
            }else {
                System.out.println("创建索引失败！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取索引
     */
    @Test
    public void getIndex() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost",9200)
                )
        );
        GetIndexRequest request = new GetIndexRequest("test_index");
        try {
            GetIndexResponse response = client.indices().get(request, RequestOptions.DEFAULT);
            String[] indices = response.getIndices();
            for (String index : indices) {
                System.out.println(index);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除索引
     */
    @Test
    public void delIndex() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost",9200)
                )
        );
        DeleteIndexRequest request = new DeleteIndexRequest("teacher");
        try {
            AcknowledgedResponse response = client.indices().delete(request,RequestOptions.DEFAULT);
            if (response.isAcknowledged()) {
                System.out.println("删除索引成功！");
            }else {
                System.out.println("删除索引失败！");
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入数据
     */
    @Test
    public void insertData() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost",9200)
                )
        );
        List<Teacher> list = teacherMapper.query();
        IndexRequest request = new IndexRequest("teacher");
        Gson gson = new Gson();
        Teacher teacher = list.get(0);
        request.id(teacher.gettId());
        request.source(gson.toJson(teacher), XContentType.JSON);
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            System.out.println(response);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量插入数据
     */
    @Test
    public void batchInsertData() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost",9200)
                )
        );
        BulkRequest request = new BulkRequest("teacher");
        Teacher teacher = new Teacher();
        Gson gson = new Gson();
        for (int i = 0; i < 10; i++) {
            teacher.settName("Obtio" + i);
            request.add(new IndexRequest().source(gson.toJson(teacher),XContentType.JSON));
        }
        try {
            BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);
            System.out.println(responses.getItems().length);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过Id查询
     */
    @Test
    public void getById() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost",9200)
                )
        );
        GetRequest request = new GetRequest("teacher", "qLebU38BTYy3Ur8Sh1wO");
        String[] includes = {"tName"};
        try {
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            System.out.println(response);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过ID批量获取
     */
    @Test
    public void multiGetById() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost",9200)
                )
        );
        MultiGetRequest request = new MultiGetRequest();
        request.add("teacher","qLebU38BTYy3Ur8Sh1wO");
        request.add("teacher","rLebU38BTYy3Ur8Sh1wO");
        try {
            MultiGetResponse responses = client.mget(request, RequestOptions.DEFAULT);
            for (MultiGetItemResponse respons : responses) {
                System.out.println(respons.getResponse().getSourceAsString());
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateByQuery() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost",9200)
                )
        );
        UpdateByQueryRequest request = new UpdateByQueryRequest("teacher");
        request.setScript(
          new Script(ScriptType.INLINE,"painless","ctx._source.tName += '111'", Collections.emptyMap())
        );
        try {
            BulkByScrollResponse response = client.updateByQuery(request, RequestOptions.DEFAULT);
            System.out.println(response);
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }





}
